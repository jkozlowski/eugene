package eugene.market.esma.impl.behaviours;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.Repository.Tuple;
import eugene.market.esma.impl.execution.Execution;
import eugene.market.esma.impl.execution.data.CancelOrderEvent;
import eugene.market.esma.impl.execution.data.ExecutionEvent;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.esma.impl.execution.data.MarketDataEvent;
import eugene.market.esma.impl.execution.data.MarketDataEventHandler;
import eugene.market.esma.impl.execution.data.NewOrderEvent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
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
import static eugene.market.esma.impl.Messages.deleteOrder;
import static eugene.market.esma.impl.Messages.executionReport;
import static eugene.market.esma.impl.Messages.orderExecuted;
import static eugene.market.esma.impl.Orders.addOrder;
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

        if (order.getOrdType().isLimit()) {
            send(addOrder(order, symbol));
        }
    }

    @Override
    public void handle(final ExecutionEvent executionEvent) {

        final Execution execution = executionEvent.getObject();

        // Buy side
        final OrderStatus buyOrderStatus = execution.getBuyOrderStatus();
        final Tuple buyTuple = repository.get(buyOrderStatus.getOrder());
        checkNotNull(buyTuple);

        send(executionReport(buyOrderStatus, buyTuple, symbol), buyTuple);
        if (buyOrderStatus.getOrder().getOrdType().isLimit()) {
            send(orderExecuted(buyOrderStatus, execution));
        }

        // Sell side
        final OrderStatus sellOrderStatus = execution.getSellOrderStatus();
        final Tuple sellTuple = repository.get(sellOrderStatus.getOrder());
        checkNotNull(sellTuple);

        send(executionReport(sellOrderStatus, sellTuple, symbol), sellTuple);
        if (sellOrderStatus.getOrder().getOrdType().isLimit()) {
            send(orderExecuted(sellOrderStatus, execution));
        }
    }

    @Override
    public void handle(final CancelOrderEvent cancelOrderEvent) {
        send(deleteOrder(cancelOrderEvent.getObject()));
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
            final Action a = new Action(tuple.getAID(), executionReport);

            final ACLMessage aclMessage = new ACLMessage(INFORM);
            aclMessage.addReceiver(tuple.getAID());
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
    private void send(final Message message) {

        checkNotNull(message);
        for (final AID aid : repository.getAIDs()) {
            try {
                final Action a = new Action(aid, message);

                final ACLMessage aclMessage = new ACLMessage(INFORM);
                aclMessage.addReceiver(aid);
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
}
