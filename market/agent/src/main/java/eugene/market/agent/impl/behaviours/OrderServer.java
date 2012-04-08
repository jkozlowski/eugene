/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.behaviours;

import com.google.common.base.Optional;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import eugene.market.agent.MarketAgent;
import eugene.market.agent.impl.Orders;
import eugene.market.agent.impl.Orders.NewOrderSingleValidationException;
import eugene.market.agent.impl.Repository;
import eugene.market.agent.impl.Repository.Tuple;
import eugene.market.agent.impl.execution.ExecutionEngine;
import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
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
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

/**
 * Accepts {@link Message}s for execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderServer {

    private final Meter requests = Metrics.newMeter(OrderServer.class, "message-rate", "messages", TimeUnit.SECONDS);

    private final Gauge<Integer> queueSize
            = Metrics.newGauge(OrderServer.class, "pending-messages",
                               new Gauge<Integer>() {
                                   @Override
                                   public Integer value() {
                                       return agent.getQueueSize();
                                   }
                               });

    private final Timer responses = Metrics.newTimer(OrderServer.class, "response-time", TimeUnit.MILLISECONDS,
                                                     TimeUnit.SECONDS);

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

        final TimerContext context = responses.time();
        try {
            queueSize.value();
            requests.mark();
            final Order newOrder = Orders.newOrder(executionEngine, newOrderSingle);
            final String clOrdID = newOrderSingle.getClOrdID().getValue();
            repository.put(newOrder, new Tuple(request, clOrdID));
            executionEngine.execute(newOrder);
        }
        catch (NewOrderSingleValidationException e) {
            rejectOrder(newOrderSingle, request);
        }
        finally {
            context.stop();
        }
    }

    /**
     * Accepts {@link OrderCancelRequest} messages.
     *
     * @param orderCancelRequest message to accept.
     * @param request            original request.
     */
    public void serveOrderCancelRequestRequest(final OrderCancelRequest orderCancelRequest, final ACLMessage request) {

        final TimerContext context = responses.time();
        try {
            queueSize.value();
            requests.mark();
            final Tuple tuple = new Tuple(request, orderCancelRequest.getClOrdID().getValue());
            final Order order = repository.get(tuple);

            if (null == order) {
                rejectCancel(orderCancelRequest, request);
                return;
            }

            final Optional<OrderStatus> orderStatus = executionEngine.cancel(order);

            if (!orderStatus.isPresent()) {
                rejectCancel(orderCancelRequest, request);
                return;
            }
        }
        catch (IllegalArgumentException e) {
            rejectCancel(orderCancelRequest, request);
        }
        catch (NullPointerException e) {
            rejectCancel(orderCancelRequest, request);
        }
        finally {
            context.stop();
        }
    }

    /**
     * Accepts {@link Logon} messages.
     *
     * @param logon   message to accept.
     * @param request original message.
     */
    public void serveLogonRequest(final Logon logon, final ACLMessage request) {

        final TimerContext context = responses.time();
        try {
            queueSize.value();
            requests.mark();

            final Logon logonReply = new Logon();
            logonReply.setSymbol(new Symbol(symbol));

            if (symbol.equals(logon.getSymbol().getValue())) {
                repository.add(request.getSender());
                logonReply.setSessionStatus(SessionStatus.SESSION_ACTIVE.field());
            }

            send(request, logonReply);
        }
        finally {
            context.stop();
        }
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
            agent.getContentManager().fillContent(aclMessage, a);
            return aclMessage;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
