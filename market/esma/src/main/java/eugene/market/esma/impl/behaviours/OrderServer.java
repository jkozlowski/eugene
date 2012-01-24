package eugene.market.esma.impl.behaviours;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.esma.MarketAgent;
import eugene.market.esma.impl.Orders.NewOrderSingleValidationException;
import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.Repository.Tuple;
import eugene.market.esma.impl.execution.ExecutionEngine;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.SessionStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.esma.impl.Orders.newOrder;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

/**
 * Methods for accepting {@link Message}s for execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderServer {

    private static Logger LOG = Logger.getMyLogger(OrderServer.class.getName());

    private final Agent agent;

    private final ExecutionEngine executionEngine;

    private final Repository repository;

    private final String symbol;

    /**
     * Constructs an instance of {@link OrderServer} for this <code>agent</code> that will use {@link
     * ExecutionEngine} and {@link Repository}.
     *
     * @param agent  owner of this {@link OrderServer}.
     * @param symbol symbol to accept.
     */
    public OrderServer(final MarketAgent agent, final String symbol) {
        this(agent, new ExecutionEngine(), new Repository(), symbol);
    }

    /**
     * Constructs an instance of {@link OrderServer} for this <code>agent</code> that will use this
     * <code>executionEngine</code> and <code>repository</code>.
     *
     * @param agent           owner of this {@link OrderServer}.
     * @param executionEngine {@link ExecutionEngine} to use.
     * @param repository      {@link Repository} to use.
     * @param symbol          symbol to accept.
     */
    public OrderServer(final MarketAgent agent, final ExecutionEngine executionEngine, final Repository repository,
                       final String symbol) {
        checkNotNull(agent);
        checkNotNull(executionEngine);
        checkNotNull(repository);
        checkNotNull(symbol);
        this.agent = agent;
        this.executionEngine = executionEngine;
        this.repository = repository;
        this.symbol = symbol;
    }

    /**
     * Accepts {@link NewOrderSingle} messages.
     *
     * @param newOrderSingle message to accept.
     * @param request        original message.
     */
    public void serveNewOrderSingleRequest(final NewOrderSingle newOrderSingle, final ACLMessage request) {
        try {
            final Order order = newOrder(newOrderSingle);

            final OrderStatus orderStatus = executionEngine.insertOrder(order);

            if (null == orderStatus) {
                rejectOrder(newOrderSingle, request);
            }
            else {
                final AID aid = request.getSender();
                final String clOrdID = newOrderSingle.getClOrdID().getValue();
                repository.put(order, new Tuple(aid, clOrdID));
                acceptOrder(orderStatus, newOrderSingle, request);
            }
        }
        catch (NewOrderSingleValidationException e) {
            rejectOrder(newOrderSingle, request);
        }
    }

    /**
     * Accepts {@link OrderCancelRequest} messages.
     *
     * @param orderCancelRequest message to accept.
     * @param request            original request.
     */
    public void serveOrderCancelRequestRequest(final OrderCancelRequest orderCancelRequest, final ACLMessage request) {

        try {
            final Tuple tuple = new Tuple(request.getSender(), orderCancelRequest.getClOrdID().getValue());
            final Order order = repository.get(tuple);

            if (null == order) {
                rejectCancel(orderCancelRequest, request);
                return;
            }

            final OrderStatus orderStatus = executionEngine.cancel(order);

            if (null == orderStatus) {
                rejectCancel(orderCancelRequest, request);
                return;
            }

            cancelOrder(orderStatus, tuple, request);
        }
        catch (IllegalArgumentException e) {
            rejectCancel(orderCancelRequest, request);
        }
        catch (NullPointerException e) {
            rejectCancel(orderCancelRequest, request);
        }
    }

    /**
     * Accepts {@link Logon} messages.
     *
     * @param logon   message to accept.
     * @param request original message.
     */
    public void serveLogonRequest(final Logon logon, final ACLMessage request) {

        final Logon logonReply = new Logon();
        logonReply.setSymbol(new Symbol(symbol));

        if (symbol.equals(logon.getSymbol().getValue())) {
            repository.add(request.getSender());
            logonReply.setSessionStatus(SessionStatus.SESSION_ACTIVE.field());
        }

        send(request, logonReply);
    }

    /**
     * Sends an {@link ExecutionReport} with {@link ExecType#NEW}.
     *
     * @param orderStatus    orderStatus to send the {@link ExecutionReport} about.
     * @param newOrderSingle {@link NewOrderSingle} from the <code>request</code>.
     * @param request        original request.
     */
    private void acceptOrder(final OrderStatus orderStatus, final NewOrderSingle newOrderSingle,
                             final ACLMessage request) {

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(orderStatus.getAvgPx()));
        executionReport.setExecType(ExecType.NEW.field());
        executionReport.setOrdStatus(OrdStatus.NEW.field());
        executionReport.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        executionReport.setCumQty(new CumQty(orderStatus.getCumQty()));
        executionReport.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        executionReport.setClOrdID(new ClOrdID(newOrderSingle.getClOrdID().getValue()));
        executionReport.setSide(orderStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));

        send(request, executionReport);
    }

    /**
     * Sends an {@link ExecutionReport} with {@link ExecType#REJECTED}.
     *
     * @param newOrderSingle {@link NewOrderSingle} from the <code>request</code>.
     * @param request        original request.
     */
    private void rejectOrder(final NewOrderSingle newOrderSingle, final ACLMessage request) {

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(Order.NO_PRICE));
        executionReport.setExecType(ExecType.REJECTED.field());
        executionReport.setOrdStatus(OrdStatus.REJECTED.field());
        executionReport.setLeavesQty(new LeavesQty(newOrderSingle.getOrderQty().getValue()));
        executionReport.setCumQty(new CumQty(Order.NO_QTY));
        executionReport.setOrderID(new OrderID(newOrderSingle.getClOrdID().getValue()));
        executionReport.setClOrdID(new ClOrdID(newOrderSingle.getClOrdID().getValue()));
        executionReport.setSide(newOrderSingle.getSide());
        executionReport.setSymbol(new Symbol(symbol));

        send(request, executionReport);
    }

    /**
     * Sends an {@link ExecutionReport} with this {@link ExecType#CANCELED}.
     *
     * @param orderStatus {@link OrderStatus} of cancelled order.
     * @param tuple       {@link Tuple} for this {@link OrderStatus}.
     * @param request     original request.
     */
    private void cancelOrder(final OrderStatus orderStatus, final Tuple tuple, final ACLMessage request) {

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(Order.NO_PRICE));
        executionReport.setExecType(ExecType.REJECTED.field());
        executionReport.setOrdStatus(OrdStatus.REJECTED.field());
        executionReport.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        executionReport.setCumQty(new CumQty(Order.NO_QTY));
        executionReport.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        executionReport.setClOrdID(new ClOrdID(tuple.getClOrdID()));
        executionReport.setSide(orderStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));

        send(request, executionReport);
    }

    /**
     * Sends an {@link OrderCancelReject}.
     *
     * @param orderCancelRequest {@link OrderCancelRequest} from the <code>request</code>.
     * @param request            original request.
     */
    private void rejectCancel(final OrderCancelRequest orderCancelRequest, final ACLMessage request) {
        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        orderCancelReject.setOrdStatus(OrdStatus.REJECTED.field());
        orderCancelReject.setClOrdID(orderCancelRequest.getClOrdID());
        orderCancelReject.setOrderID(orderCancelReject.getOrderID());
    }

    /**
     * Sends this {@link ExecutionReport} to the sender of <code>originalRequest</code>.
     *
     * @param originalRequest original {@link ACLMessage}.
     * @param executionReport {@link ExecutionReport} to send.
     */
    private void send(final ACLMessage originalRequest, final ExecutionReport executionReport) {
        final ACLMessage aclMessage = fillContent(originalRequest, executionReport);

        if (ExecType.REJECTED.field().equals(executionReport.getExecType())) {
            aclMessage.setPerformative(REFUSE);
        }
        else {
            aclMessage.setPerformative(INFORM);
        }

        agent.send(aclMessage);
    }

    /**
     * Sends this {@link Logon} to the sender of <code>originalRequest</code>.
     *
     * @param originalRequest original {@link ACLMessage}.
     * @param logon           {@link Logon} to send.
     */
    private void send(final ACLMessage originalRequest, final Logon logon) {
        final ACLMessage aclMessage = fillContent(originalRequest, logon);

        if (SessionStatus.SESSION_ACTIVE.field().equals(logon.getSessionStatus())) {
            aclMessage.setPerformative(INFORM);
        }
        else {
            aclMessage.setPerformative(REFUSE);
        }

        agent.send(aclMessage);
    }

    /**
     * Gets the reply to <code>originalRequest</code>, with <code>message</code> in the content and language,
     * ontology and receiver correctly set.
     *
     * @param originalRequest original {@link ACLMessage}.
     * @param message         {@link Message} to set the content to.
     *
     * @return reply {@link ACLMessage}.
     *
     * @throws RuntimeException if a checked exception occurs.
     */
    private ACLMessage fillContent(final ACLMessage originalRequest, final Message message) {
        try {
            checkNotNull(originalRequest);
            checkNotNull(message);
            final Action a = new Action(originalRequest.getSender(), message);
            final ACLMessage aclMessage = originalRequest.createReply();
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            aclMessage.addReceiver(originalRequest.getSender());
            agent.getContentManager().fillContent(aclMessage, a);
            return aclMessage;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
