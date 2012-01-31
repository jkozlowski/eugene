package eugene.simulation.agent.impl;

import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.utils.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Calendar;
import java.util.Set;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Sends {@link Start} messages to trader agents.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartSimulationBehaviour extends SequentialBehaviour {

    private final BehaviourResult<Calendar> result;

    private final BehaviourResult<Set<String>> guids;

    /**
     * Creates a {@link StartSimulationBehaviour} that will send {@link Start} message to <code>guids</code>.
     *
     * @param guids {@link AID#ISGUID} of agents to send {@link Start} message to.
     */
    public StartSimulationBehaviour(final BehaviourResult<Set<String>> guids) {
        checkNotNull(guids);
        this.guids = guids;
        this.result = new BehaviourResult();
    }

    @Override
    public void onStart() {
        try {
            final Action action = new Action(myAgent.getAID(), new Start());
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setLanguage(SimulationOntology.LANGUAGE);
            aclMessage.setOntology(SimulationOntology.NAME);

            myAgent.getContentManager().fillContent(aclMessage, action);

            final AchieveREInitiator sender = new AchieveREInitiator(myAgent, aclMessage) {

                @Override
                protected Vector prepareRequests(final ACLMessage request) {
                    final Vector l = new Vector(guids.getObject().size());
                    for (final String guid : guids.getObject()) {
                        final ACLMessage clone = (ACLMessage) request.clone();
                        clone.addReceiver(new AID(guid, AID.ISGUID));
                        l.addElement(clone);
                    }

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

                        result.success(Calendar.getInstance());
                    }
                    catch (CodecException e) {
                        e.printStackTrace();
                    }
                    catch (OntologyException e) {
                        e.printStackTrace();
                    }
                }

            };
            addSubBehaviour(sender);
        }
        catch (CodecException e) {
            e.printStackTrace();
        }
        catch (OntologyException e) {
            e.printStackTrace();
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
