/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import com.google.common.base.Stopwatch;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
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

import java.util.Calendar;
import java.util.Set;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Calendar.getInstance;

/**
 * Sends {@link Start} messages to trader agents.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartSimulationBehaviour extends SequentialBehaviour {

    private static final String ERROR_MSG = "Failed to start the simulation";

    private final Logger LOG = LoggerFactory.getLogger(StartSimulationBehaviour.class);

    private final int length;

    private final BehaviourResult<Calendar> result;

    private final BehaviourResult<Set<String>> guids;

    /**
     * Creates a {@link StartSimulationBehaviour} that will send {@link Start} message to <code>guids</code>.
     *
     * @param length length of the simulation in milliseconds.
     * @param guids  {@link AID#ISGUID} of agents to send {@link Start} message to.
     */
    public StartSimulationBehaviour(final int length, final BehaviourResult<Set<String>> guids) {
        checkArgument(length > 0);
        checkNotNull(guids);
        this.length = length;
        this.guids = guids;
        this.result = new BehaviourResult();
    }

    @Override
    public void onStart() {
        try {
            final Stopwatch stopwatch = new Stopwatch().start();
            
            final Calendar startCalendar = getInstance();
            final Calendar stopCalendar = (Calendar) startCalendar.clone();
            stopCalendar.add(Calendar.MILLISECOND, length);

            final Action action = new Action(myAgent.getAID(), new Start(startCalendar.getTime(),
                                                                         stopCalendar.getTime()));
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setLanguage(SimulationOntology.LANGUAGE);
            aclMessage.setOntology(SimulationOntology.NAME);

            myAgent.getContentManager().fillContent(aclMessage, action);

            final AchieveREInitiator sender = new AchieveREInitiator(myAgent, aclMessage) {

                @Override
                protected Vector prepareRequests(final ACLMessage request) {
                    final Vector l = new Vector(guids.getObject().size());
                    final ACLMessage clone = (ACLMessage) request.clone();
                    for (final String guid : guids.getObject()) {
                        clone.addReceiver(new AID(guid, AID.ISGUID));
                    }
                    l.addElement(clone);

                    return l;
                }

                @Override
                public void handleAllResultNotifications(Vector responses) {
                    try {
                        if (guids.getObject().size() != responses.size()) {
                            result.fail();
                            return;
                        }

                        for (final Object o : responses) {
                            if (!(o instanceof ACLMessage)) {
                                result.fail();
                            }

                            final ContentElement ce = myAgent.getContentManager().extractContent((ACLMessage) o);

                            if (!(ce instanceof Action) ||
                                    null == ((Action) ce).getAction() ||
                                    !(((Action) ce).getAction() instanceof Started)) {
                                result.fail();
                            }

                            if (!guids.getObject().contains(((ACLMessage) o).getSender().getName())) {
                                result.fail();
                            }
                        }

                        LOG.info("Sending Start messages took {}", stopwatch.stop());
                        result.success(startCalendar);
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

            };
            addSubBehaviour(sender);
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

    public BehaviourResult<Calendar> getResult() {
        return result;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
