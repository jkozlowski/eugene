/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListener;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.simulation.agent.Simulation;
import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.Applications.proxy;

/**
 * {@link SessionImpl} is a communication channel between the Agents and the Market Agent.
 *
 * {@link SessionImpl} sends an appropriate {@link Logon} message to the Market Agent in order to initiate the
 * communication channel. {@link SessionImpl} will route all {@link Message}s (sent through it
 * and received by it) to {@link Application}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class SessionImpl implements Session {

    private final Simulation simulation;

    private final Agent agent;

    private final Application application;

    private final OrderReferenceApplication orderReferenceApplication;

    private final AtomicLong curClOrdID = new AtomicLong(1);

    /**
     * Creates a {@link SessionImpl} that will route all messages to this <code>application</code> and will be
     * executed by this <code>agent</code>.
     *
     * @param simulation  {@link Simulation} that is being run.
     * @param agent       {@link Agent} that will execute this {@link SessionImpl}.
     * @param application implementation of {@link Application} that this {@link SessionImpl} will route {@link
     *                    Message}s to.
     */
    public SessionImpl(final Simulation simulation, final Agent agent, final Application application) {
        checkNotNull(simulation);
        checkNotNull(agent);
        checkNotNull(application);
        this.simulation = simulation;
        this.agent = agent;
        this.orderReferenceApplication = new OrderReferenceApplication();
        this.application = proxy(orderReferenceApplication, application);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<OrderReference> getOrderReferences() {
        return orderReferenceApplication.getOrderReferences();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AgentAction> T extractMessage(final ACLMessage aclMessage, final Class<T> type) {

        checkNotNull(aclMessage);
        checkNotNull(type);

        try {
            final ContentElement ce = agent.getContentManager().extractContent(aclMessage);

            if (ce instanceof Action &&
                    null != ((Action) ce).getAction() &&
                    type.isAssignableFrom(((Action) ce).getAction().getClass())) {

                return (T) ((Action) ce).getAction();
            }
        }
        catch (CodecException e) {
        }
        catch (OntologyException e) {
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ACLMessage aclRequest(final Message message) {

        checkNotNull(message);

        try {
            final Action action = new Action(simulation.getMarketAgent(), message);
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            aclMessage.addReceiver(simulation.getMarketAgent());

            agent.getContentManager().fillContent(aclMessage, action);

            return aclMessage;
        }
        catch (CodecException e) {
            throw new RuntimeException(e);
        }
        catch (OntologyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderReference send(final NewOrderSingle newOrderSingle)
            throws NullPointerException, IllegalArgumentException {
        return send(newOrderSingle, OrderReferenceListener.EMPTY_LISTENER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderReference send(final NewOrderSingle newOrderSingle, final OrderReferenceListener listener)
            throws NullPointerException, IllegalArgumentException {
        checkNotNull(newOrderSingle);
        checkNotNull(listener);
        checkArgument(null == newOrderSingle.getSymbol() ||
                              simulation.getSymbol().equals(newOrderSingle.getSymbol().getValue()));
        checkArgument(null == newOrderSingle.getClOrdID());

        newOrderSingle.setSymbol(new Symbol(simulation.getSymbol()));

        final String clOrdID = getClOrdID();

        newOrderSingle.setClOrdID(new ClOrdID(clOrdID));

        application.fromApp(newOrderSingle, this);

        final OrderReferenceImpl ref = new OrderReferenceImpl(listener, clOrdID, System.currentTimeMillis(),
                                                              OrdType.getOrdType(newOrderSingle),
                                                              Side.getSide(newOrderSingle),
                                                              newOrderSingle.getOrderQty().getValue(),
                                                              null == newOrderSingle.getPrice() ? Order.NO_PRICE :
                                                                      newOrderSingle.getPrice().getValue());
        orderReferenceApplication.addOrderReference(ref);

        agent.send(aclRequest(newOrderSingle));

        return ref;
    }

    @Override
    public void send(OrderCancelRequest orderCancelRequest) throws NullPointerException, IllegalArgumentException {
        checkNotNull(orderCancelRequest);
        checkArgument(null == orderCancelRequest.getSymbol() ||
                              simulation.getSymbol().equals(orderCancelRequest.getSymbol().getValue()));
        orderCancelRequest.setSymbol(new Symbol(simulation.getSymbol()));
        application.fromApp(orderCancelRequest, this);
        agent.send(aclRequest(orderCancelRequest));
    }

    @VisibleForTesting
    public OrderReferenceApplication getOrderReferenceApplication() {
        return orderReferenceApplication;
    }

    @VisibleForTesting
    Long getCurClOrdID() {
        return curClOrdID.get();
    }

    private String getClOrdID() {
        final StringBuilder b = new StringBuilder(agent.getAID().getLocalName());
        b.append(curClOrdID.getAndIncrement());
        return b.toString();
    }
}
