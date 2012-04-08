/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.agent.MarketAgent;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Stop;
import eugene.simulation.ontology.Stopped;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Set;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Stops the simulation after some time period.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StopSimulationBehaviour extends SequentialBehaviour {

    private static final String ERROR_MSG = "Failed to stop the simulation";

    private final Logger LOG = LoggerFactory.getLogger(StopSimulationBehaviour.class);

    private final BehaviourResult result;

    private final BehaviourResult<Calendar> start;
    
    private final int length;

    private final BehaviourResult<Set<String>> guids;

    private final BehaviourResult<AID> marketAgent;

    /**
     * Creates a {@link StopSimulationBehaviour} that will stop the simulation after <code>length</code> milliseconds
     * from <code>start</code>.
     *
     * @param start       start of the simulation.
     * @param length      length of the simulation in milliseconds.
     * @param guids       agents to stop.
     * @param marketAgent {@link AID} of the {@link MarketAgent}.
     */
    public StopSimulationBehaviour(final BehaviourResult<Calendar> start, int length, 
                                   final BehaviourResult<Set<String>> guids, final BehaviourResult<AID> marketAgent) {
        checkNotNull(start);
        checkArgument(length > 0);
        checkNotNull(guids);
        checkNotNull(marketAgent);
        this.start = start;
        this.length = length;
        this.guids = guids;
        this.marketAgent = marketAgent;
        this.result = new BehaviourResult();
    }

    @Override
    public void onStart() {
        try {
            final Action action = new Action(myAgent.getAID(), new Stop());
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setLanguage(SimulationOntology.LANGUAGE);
            aclMessage.setOntology(SimulationOntology.NAME);

            myAgent.getContentManager().fillContent(aclMessage, action);

            final AchieveREInitiator sender = new AchieveREInitiator(myAgent, aclMessage) {

                @Override
                protected Vector prepareRequests(final ACLMessage request) {
                    final Vector l = new Vector(guids.getObject().size());
                    final ACLMessage clone = (ACLMessage) request.clone();
                    clone.addReceiver(marketAgent.getObject());

                    for (final String guid : guids.getObject()) {
                        clone.addReceiver(new AID(guid, AID.ISGUID));
                    }

                    l.addElement(clone);

                    return l;
                }

                @Override
                public void handleAllResultNotifications(Vector responses) {
                    try {
                        if (guids.getObject().size() + 1 != responses.size()) {
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
                                    !(((Action) ce).getAction() instanceof Stopped)) {
                                result.fail();
                            }

                            if (!guids.getObject().contains(((ACLMessage) o).getSender().getName()) &&
                                    !marketAgent.equals(((ACLMessage) o).getSender())) {
                                result.fail();
                            }
                        }

                        result.success();
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

            final Calendar stop = Calendar.getInstance();
            stop.setTime(start.getObject().getTime());
            stop.add(Calendar.MILLISECOND, length);

            addSubBehaviour(new WakerBehaviour(myAgent, stop.getTime()) {
                @Override
                protected void onWake() {
                }
            });
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
    
    @Override
    public int onEnd() {
        return result.getResult();
    }
}
