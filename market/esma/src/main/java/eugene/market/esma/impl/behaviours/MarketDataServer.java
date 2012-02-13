package eugene.market.esma.impl.behaviours;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.esma.impl.Messages.addOrder;
import static eugene.market.esma.impl.Messages.deleteOrder;
import static eugene.market.esma.impl.Messages.executionReport;
import static eugene.market.esma.impl.Messages.orderExecuted;
import static eugene.market.esma.impl.Messages.tradeExecutionReport;
import static jade.lang.acl.ACLMessage.INFORM;

/**
 * Sends out {@link MarketDataEvent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataServer extends CyclicBehaviour implements MarketDataEventHandler {

    private final Logger LOG = LoggerFactory.getLogger(MarketDataServer.class);

    private final Marker NEWORDER = MarkerFactory.getMarker("NEWORDER");

    private final Marker REJECTORDER = MarkerFactory.getMarker("REJECTORDER");

    private final Marker EXECUTION = MarkerFactory.getMarker("EXECUTION");

    private final Marker ADDORDER = MarkerFactory.getMarker("ADDORDER");

    private final Marker CANCELORDER = MarkerFactory.getMarker("CANCELORDER");

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

        final OrderStatus orderStatus = newOrderEvent.getObject();
        final Tuple orderTuple = repository.get(orderStatus.getOrder());
        checkNotNull(orderTuple);

        send(executionReport(orderStatus, orderTuple, symbol), orderTuple);

        LOG.info(NEWORDER,
                 "{},{},{},{},{},{},{},{}",
                 new Object[]{
                         newOrderEvent.getTime(),
                         orderTuple.getACLMessage().getSender().getLocalName(),
                         orderStatus.getOrder().getOrderID(),
                         orderTuple.getClOrdID(),
                         orderStatus.getOrder().getOrdType(),
                         orderStatus.getOrder().getSide(),
                         orderStatus.getOrder().getOrderQty(),
                         orderStatus.getOrder().getPrice()
                 }
        );
    }

    @Override
    public void handle(final RejectOrderEvent rejectOrderEvent) {

        final OrderStatus orderStatus = rejectOrderEvent.getObject();
        final Tuple orderTuple = repository.get(orderStatus.getOrder());
        checkNotNull(orderTuple);

        send(executionReport(orderStatus, orderTuple, symbol), orderTuple);

        LOG.info(REJECTORDER,
                 "{},{},{},{},{},{},{},{}",
                 new Object[]{
                         rejectOrderEvent.getTime(),
                         orderTuple.getACLMessage().getSender().getLocalName(),
                         orderStatus.getOrder().getOrderID(),
                         orderTuple.getClOrdID(),
                         orderStatus.getOrder().getOrdType(),
                         orderStatus.getOrder().getSide(),
                         orderStatus.getOrder().getOrderQty(),
                         orderStatus.getOrder().getPrice()
                 }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final AddOrderEvent addOrderEvent) {
        sendToAll(addOrder(addOrderEvent.getObject(), symbol));

        LOG.info(ADDORDER,
                 "{},{},{},{},{}",
                 new Object[]{
                         addOrderEvent.getTime(),
                         addOrderEvent.getObject().getOrder().getOrderID(),
                         addOrderEvent.getObject().getOrder().getSide(),
                         addOrderEvent.getObject().getOrder().getOrderQty(),
                         addOrderEvent.getObject().getOrder().getPrice()
                 }
        );
    }

    @Override
    public void handle(final ExecutionEvent executionEvent) {

        final Execution execution = executionEvent.getObject();

        // New order
        final OrderStatus newOrderStatus = execution.getNewOrderStatus();
        final Tuple newOrderTuple = repository.get(newOrderStatus.getOrder());
        checkNotNull(newOrderTuple);

        send(tradeExecutionReport(newOrderStatus, execution, newOrderTuple, symbol), newOrderTuple);

        // Limit order
        final OrderStatus limitOrderStatus = execution.getLimitOrderStatus();
        final Tuple limitOrderTuple = repository.get(limitOrderStatus.getOrder());
        checkNotNull(limitOrderTuple);

        send(tradeExecutionReport(limitOrderStatus, execution, limitOrderTuple, symbol), limitOrderTuple);
        sendToAll(orderExecuted(limitOrderStatus, execution));

        LOG.info(EXECUTION,
                 "{},{},{},{},{},{},{}",
                 new Object[]{
                         executionEvent.getTime(),
                         newOrderStatus.getOrder().getOrderID(),
                         newOrderStatus.getOrder().getSide(),
                         limitOrderStatus.getOrder().getOrderID(),
                         limitOrderStatus.getOrder().getSide(),
                         execution.getQuantity(),
                         execution.getPrice()
                 }
        );
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

        LOG.info(CANCELORDER,
                 "{},{},{},{},{},{},{}",
                 new Object[]{
                         cancelOrderEvent.getTime(),
                         cancelTuple.getACLMessage().getSender().getLocalName(),
                         cancelOrderEvent.getObject().getOrder().getOrderID(),
                         cancelOrderEvent.getObject().getOrder().getOrdType(),
                         cancelOrderEvent.getObject().getOrder().getSide(),
                         cancelOrderEvent.getObject().getOrder().getOrderQty(),
                         cancelOrderEvent.getObject().getOrder().getPrice()
                 }
        );
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
            LOG.error("Failed to send {}", executionReport, e);
        }
        catch (OntologyException e) {
            LOG.error("Failed to send {}", executionReport, e);
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
            LOG.error("Failed to send to all {}", message, e);
        }
        catch (OntologyException e) {
            LOG.error("Failed to send to all {}", message, e);
        }
    }
}
