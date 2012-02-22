/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.book.Order;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.synchronizedSet;

/**
 * Sends the initial orders to initialize the order book.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class BootstrapOrderBookBehaviour extends SequentialBehaviour {

    private static final String ERROR_MSG = "Failed to bootstrap the Order Book";

    private final Logger LOG = LoggerFactory.getLogger(BootstrapOrderBookBehaviour.class);

    private final BehaviourResult<Set<ExecutionReport>> result;

    private final BehaviourResult<AID> marketAgent;

    private final String symbol;

    private final Set<Order> orders;

    /**
     * Sends the <code>orders</code> to the <code>marketAgent</code>.
     *
     * @param marketAgent {@link AID} of the {@link MarketAgent}.
     * @param symbol      symbol for this simulation.
     * @param orders      orders to send.
     */
    public BootstrapOrderBookBehaviour(final BehaviourResult<AID> marketAgent, final String symbol,
                                       final Set<Order> orders) {
        checkNotNull(marketAgent);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        checkNotNull(orders);
        checkArgument(!orders.isEmpty());

        for (final Order order : orders) {
            checkArgument(order.getOrdType().isLimit());
        }

        this.marketAgent = marketAgent;
        this.symbol = symbol;
        this.orders = synchronizedSet(orders);
        this.result = new BehaviourResult<Set<ExecutionReport>>();
    }

    @Override
    public void onStart() {
        try {
            final Set<ExecutionReport> received = newHashSet();
            int i = 1;
            for (final Order order : orders) {

                final ClOrdID clOrdID = new ClOrdID(myAgent.getName() + i++);
                final NewOrderSingle newOrder = new NewOrderSingle();
                newOrder.setOrdType(order.getOrdType().field());
                newOrder.setPrice(new Price(order.getPrice()));
                newOrder.setSide(order.getSide().field());
                newOrder.setClOrdID(clOrdID);
                newOrder.setOrderQty(new OrderQty(order.getOrderQty()));
                newOrder.setSymbol(new Symbol(symbol));

                final Action a = new Action(marketAgent.getObject(), newOrder);
                final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(marketAgent.getObject());
                aclMessage.setLanguage(MarketOntology.LANGUAGE);
                aclMessage.setOntology(MarketOntology.getInstance().getName());

                myAgent.getContentManager().fillContent(aclMessage, a);

                addSubBehaviour(new AchieveREInitiator(myAgent, aclMessage) {
                    @Override
                    public void handleInform(final ACLMessage inform) {
                        try {
                            final ContentElement ce = myAgent.getContentManager().extractContent(inform);

                            if (!(ce instanceof Action) ||
                                    null == ((Action) ce).getAction() ||
                                    !(((Action) ce).getAction() instanceof ExecutionReport) ||
                                    !(((ExecutionReport) ((Action) ce).getAction()).getClOrdID().equals(clOrdID)) ||
                                    !(((ExecutionReport) ((Action) ce).getAction()).getExecType().equals(ExecType.NEW
                                                                                                                 .field())
                                    ) ||
                                    !(((ExecutionReport) ((Action) ce).getAction()).getOrdStatus().equals(OrdStatus.NEW
                                                                                                                  .field())
                                    )) {
                                result.fail();
                            }

                            received.add((ExecutionReport) ((Action) ce).getAction());

                            if (orders.size() == received.size()) {
                                result.success(received);
                            }

                        }
                        catch (CodecException e) {
                            LOG.error(ERROR_MSG, e);
                            result.fail();
                        }
                        catch (OntologyException e) {
                            LOG.error(ERROR_MSG, e);
                            result.fail();
                        }
                    }
                });

            }
        }
        catch (CodecException e) {
            result.fail();
            LOG.error(ERROR_MSG, e);
        }
        catch (OntologyException e) {
            result.fail();
            LOG.error(ERROR_MSG, e);
        }
    }

    public BehaviourResult<Set<ExecutionReport>> getResult() {
        return result;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
