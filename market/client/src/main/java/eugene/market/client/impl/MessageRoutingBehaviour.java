/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import static jade.lang.acl.MessageTemplate.MatchLanguage;
import static jade.lang.acl.MessageTemplate.MatchOntology;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;

/**
 * Receives {@link Message}s and routes them to appropriate callback in {@link Application}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class MessageRoutingBehaviour extends CyclicBehaviour {

    private final MessageTemplate template;

    private final Session session;

    /**
     * Creates a {@link MessageRoutingBehaviour} that will route messages as part of this <code>session</code>.
     *
     * @param session owner of this {@link MessageRoutingBehaviour}.
     */
    public MessageRoutingBehaviour(final Session session) {
        checkNotNull(session);
        this.session = session;
        this.template = and(MatchLanguage(MarketOntology.LANGUAGE),
                            and(MatchOntology(MarketOntology.getInstance().getName()),
                                MatchSender(session.getSimulation().getMarketAgent())));
    }

    @Override
    public void action() {

        final ACLMessage aclMessage = myAgent.receive(template);

        if (null == aclMessage) {
            return;
        }

        final Message message = session.extractMessage(aclMessage, Message.class);
        if (null == message) {
            return;
        }

        if (message instanceof ExecutionReport) {
            session.getApplication().toApp((ExecutionReport) message, session);
        }

        if (message instanceof OrderCancelReject) {
            session.getApplication().toApp((OrderCancelReject) message, session);
        }

        if (message instanceof AddOrder) {
            session.getApplication().toApp((AddOrder) message, session);
        }

        if (message instanceof DeleteOrder) {
            session.getApplication().toApp((DeleteOrder) message, session);
        }

        if (message instanceof OrderExecuted) {
            session.getApplication().toApp((OrderExecuted) message, session);
        }
    }
}
