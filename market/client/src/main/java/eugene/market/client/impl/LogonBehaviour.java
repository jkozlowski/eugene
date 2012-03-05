/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.SessionStatus;
import eugene.market.ontology.message.Logon;
import eugene.simulation.ontology.LogonComplete;
import eugene.simulation.ontology.SimulationOntology;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Sends the {@link Logon} message to the {@link MarketAgent}. If the logon is successful,
 * the returned {@link Logon} message is routed to the {@link Application} and the behaviour adds {@link
 * MessageRoutingBehaviour} for execution by the Agent and finishes with {@link BehaviourResult#SUCCESS}. Otherwise,
 * it finishes with {@link BehaviourResult#FAILURE}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class LogonBehaviour extends SequentialBehaviour {
    
    private final Logger LOG = Logger.getLogger(LogonBehaviour.class.getName());

    private final BehaviourResult result = new BehaviourResult();

    private final Session session;

    /**
     * Creates a {@link LogonBehaviour} that will logon using this <code>session</code>.
     *
     * @param session owner of this {@link LogonBehaviour}.
     */
    public LogonBehaviour(final Session session) {
        checkNotNull(session);
        this.session = session;
    }

    @Override
    public void onStart() {
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(session.getSimulation().getSymbol().getName()));
        final ACLMessage logonRequest = session.aclRequest(logon);
        addSubBehaviour(new AchieveREInitiator(myAgent, logonRequest) {
            @Override
            public void handleInform(final ACLMessage inform) {
                final Logon logon = session.extractMessage(inform, Logon.class);
                if (null != logon && SessionStatus.SESSION_ACTIVE.field().equals(logon.getSessionStatus())) {
                    logonComplete();
                }
            }
        });
    }

    private void logonComplete() {
        try {
            final ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.setOntology(SimulationOntology.getInstance().getName());
            reply.setLanguage(SimulationOntology.LANGUAGE);
            reply.addReceiver(session.getSimulation().getSimulationAgent());

            final Action a = new Action(session.getSimulation().getSimulationAgent(), new LogonComplete());

            myAgent.getContentManager().fillContent(reply, a);
            myAgent.send(reply);
            result.success();
        }
        catch (CodecException e) {
            LOG.severe(e.toString());
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
