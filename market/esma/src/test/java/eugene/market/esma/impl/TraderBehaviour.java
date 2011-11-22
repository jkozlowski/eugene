package eugene.market.esma.impl;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility Trader {@link Behaviour} for testing {@link MarketServerImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class TraderBehaviour extends SequentialBehaviour {

    public final Map<NewOrderSingle, ExecutionReport> received = new HashMap<NewOrderSingle, ExecutionReport>();

    public final Set<NewOrderSingle> failed = new HashSet<NewOrderSingle>();

    public TraderBehaviour(final Agent agent, final Set<NewOrderSingle> toSend) {
        super(agent);
        for (final NewOrderSingle newOrderSingle : toSend) {

            try {
                final Action a =
                        new Action(new AID(MarketServerImplTest.MARKET_AGENT, AID.ISLOCALNAME), newOrderSingle);
                final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(new AID(MarketServerImplTest.MARKET_AGENT, AID.ISLOCALNAME));
                aclMessage.setOntology(MarketOntology.getInstance().getName());
                aclMessage.setLanguage(MarketServerImplTest.LANGUAGE);
                myAgent.getContentManager().fillContent(aclMessage, a);
                addSubBehaviour(new AchieveREInitiator(agent, aclMessage) {

                    @Override
                    public void handleInform(ACLMessage inform) {

                        try {
                            final ContentElement a = myAgent.getContentManager().extractContent(inform);

                            if (!(a instanceof Action) || !(((Action) a).getAction() instanceof ExecutionReport)) {
                                failed.add(newOrderSingle);
                                return;
                            }

                            final ExecutionReport executionReport = (ExecutionReport) ((Action) a).getAction();
                            received.put(newOrderSingle, executionReport);
                        }
                        catch (CodecException e) {
                            failed.add(newOrderSingle);
                        }
                        catch (OntologyException e) {
                            failed.add(newOrderSingle);
                        }
                    }

                    @Override
                    public void handleRefuse(ACLMessage inform) {
                        failed.add(newOrderSingle);
                    }
                });
            }
            catch (CodecException e) {
                failed.add(newOrderSingle);
            }
            catch (OntologyException e) {
                failed.add(newOrderSingle);
            }
        }
    }
}
