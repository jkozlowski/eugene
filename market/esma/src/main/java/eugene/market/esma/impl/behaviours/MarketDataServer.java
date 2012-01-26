package eugene.market.esma.impl.behaviours;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.Repository.Tuple;
import eugene.market.esma.impl.execution.Execution;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.esma.impl.execution.data.MarketDataEvent;
import eugene.market.esma.impl.execution.data.MarketDataEvent.AddOrderEvent;
import eugene.market.esma.impl.execution.data.MarketDataEvent.CancelOrderEvent;
import eugene.market.esma.impl.execution.data.MarketDataEvent.ExecutionEvent;
import eugene.market.esma.impl.execution.data.MarketDataEvent.NewOrderEvent;
import eugene.market.esma.impl.execution.data.MarketDataEvent.RejectOrderEvent;
import eugene.market.esma.impl.execution.data.MarketDataEventHandler;
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
import eugene.market.ontology.message.ExecutionReport;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.esma.impl.Messages.addOrder;
import static eugene.market.esma.impl.Messages.deleteOrder;
import static eugene.market.esma.impl.Messages.executionReport;
import static eugene.market.esma.impl.Messages.orderExecuted;
import static jade.lang.acl.ACLMessage.INFORM;

/**
 * Sends out {@link MarketDataEvent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataServer extends CyclicBehaviour implements MarketDataEventHandler {

    private static Logger LOG = Logger.getMyLogger(OrderServer.class.getName());

    private final Agent agent;

    private final MarketDataEngine marketDataEngine;

    private final Repository repository;

    private final String symbol;

    private final AtomicLong currentEventId;

    /**
     * Constructs a {@link MarketDataServer} for this <code>agent</code> that sends out {@link MarketDataEvent}s
     * about this <code>symbol</code> from
     * this <code>marketDataEngine</code> to traders registered with this {@link Repository}.
     *
     * @param agent            owner of this {@link OrderServer}.
     * @param marketDataEngine {@link MarketDataEngine} that will generate {@link MarketDataEvent}s.
     * @param repository       {@link Repository} of registered traders.
     */
    public MarketDataServer(final Agent agent, final MarketDataEngine marketDataEngine, final Repository repository,
                            final String symbol) {
        checkNotNull(agent);
        checkNotNull(marketDataEngine);
        checkNotNull(repository);
        checkNotNull(symbol);
        this.agent = agent;
        this.marketDataEngine = marketDataEngine;
        this.repository = repository;
        this.currentEventId = new AtomicLong(marketDataEngine.getCurrentEventId());
        this.symbol = symbol;
    }

    @Override
    public void action() {

        while (null != marketDataEngine.getMarketDataEvent(currentEventId.get())) {
            final Long eventId = currentEventId.getAndIncrement();
            marketDataEngine.getMarketDataEvent(eventId).accept(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final NewOrderEvent newOrderEvent) {

        final Order order = newOrderEvent.getObject();
        final Tuple orderTuple = repository.get(order);
        checkNotNull(orderTuple);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(Order.NO_PRICE));
        executionReport.setExecType(ExecType.NEW.field());
        executionReport.setOrdStatus(OrdStatus.NEW.field());
        executionReport.setLeavesQty(new LeavesQty(order.getOrderQty()));
        executionReport.setCumQty(new CumQty(Order.NO_QTY));
        executionReport.setOrderID(new OrderID(order.getOrderID().toString()));
        executionReport.setClOrdID(new ClOrdID(orderTuple.getClOrdID()));
        executionReport.setSide(order.getSide().field());
        executionReport.setSymbol(new Symbol(symbol));

        send(executionReport, orderTuple);
    }

    @Override
    public void handle(final RejectOrderEvent rejectOrderEvent) {
        
        final Order order = rejectOrderEvent.getObject();
        final Tuple orderTuple = repository.get(order);
        checkNotNull(orderTuple);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(Order.NO_PRICE));
        executionReport.setExecType(ExecType.REJECTED.field());
        executionReport.setOrdStatus(OrdStatus.REJECTED.field());
        executionReport.setLeavesQty(new LeavesQty(order.getOrderQty()));
        executionReport.setCumQty(new CumQty(Order.NO_QTY));
        executionReport.setOrderID(new OrderID(order.getOrderID().toString()));
        executionReport.setClOrdID(new ClOrdID(orderTuple.getClOrdID()));
        executionReport.setSide(order.getSide().field());
        executionReport.setSymbol(new Symbol(symbol));

        send(executionReport, orderTuple);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final AddOrderEvent addOrderEvent) {
        sendToAll(addOrder(addOrderEvent.getObject(), symbol));
    }

    @Override
    public void handle(final ExecutionEvent executionEvent) {

        final Execution execution = executionEvent.getObject();

        // New order
        final OrderStatus newOrderStatus = execution.getNewOrderStatus();
        final Tuple buyTuple = repository.get(newOrderStatus.getOrder());
        checkNotNull(buyTuple);

        send(executionReport(newOrderStatus, buyTuple, symbol), buyTuple);

        // Limit order
        final OrderStatus limitOrderStatus = execution.getLimitOrderStatus();
        final Tuple sellTuple = repository.get(limitOrderStatus.getOrder());
        checkNotNull(sellTuple);

        send(executionReport(limitOrderStatus, sellTuple, symbol), sellTuple);
        sendToAll(orderExecuted(limitOrderStatus, execution));
    }

    @Override
    public void handle(final CancelOrderEvent cancelOrderEvent) {

        final OrderStatus cancelStatus = cancelOrderEvent.getObject();
        final Tuple cancelTuple = repository.get(cancelStatus.getOrder());
        checkNotNull(cancelTuple);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setAvgPx(new AvgPx(cancelStatus.getAvgPx()));
        executionReport.setExecType(ExecType.CANCELED.field());
        executionReport.setOrdStatus(OrdStatus.CANCELED.field());
        executionReport.setLeavesQty(new LeavesQty(cancelStatus.getLeavesQty()));
        executionReport.setCumQty(new CumQty(cancelStatus.getCumQty()));
        executionReport.setOrderID(new OrderID(cancelStatus.getOrder().getOrderID().toString()));
        executionReport.setClOrdID(new ClOrdID(cancelTuple.getClOrdID()));
        executionReport.setSide(cancelStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));
        
        send(executionReport, cancelTuple);

        if (cancelOrderEvent.getObject().getOrder().getOrdType().isLimit()) {
            sendToAll(deleteOrder(cancelOrderEvent.getObject()));
        }
    }

    /**
     * Sends this <code>executionReport</code> to the {@link AID} from this <code>tuple</code>.
     *
     * @param executionReport {@link ExecutionReport} to send.
     * @param tuple           received of this <code>executionReport</code>.
     */
    private void send(final ExecutionReport executionReport, final Tuple tuple) {
        checkNotNull(executionReport);
        checkNotNull(tuple);

        try {
            final Action a = new Action(tuple.getACLMessage().getSender(), executionReport);

            final ACLMessage aclMessage = tuple.getACLMessage().createReply();
            aclMessage.setPerformative(INFORM);
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            agent.getContentManager().fillContent(aclMessage, a);
            agent.send(aclMessage);
        }
        catch (CodecException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        catch (OntologyException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Sends this <code>message</code> to all aids returned from {@link Repository#getAIDs()}.
     *
     * @param message {@link Message} to send.
     */
    private void sendToAll(final Message message) {

        checkNotNull(message);

        try {
            final Action a = new Action(agent.getAID(), message);
            final ACLMessage aclMessage = new ACLMessage(INFORM);
            for (final AID aid : repository.getAIDs()) {
                aclMessage.addReceiver(aid);
            }
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            agent.getContentManager().fillContent(aclMessage, a);
            agent.send(aclMessage);
        }
        catch (CodecException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        catch (OntologyException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }
}
