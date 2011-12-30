package eugene.market.esma;

import eugene.market.esma.behaviours.OrderServer;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.concurrent.atomic.AtomicReference;

import static eugene.market.esma.AbstractMarketAgentTest.MARKET_AGENT;

/**
 * Utility Trader {@link Behaviour} for testing {@link OrderServer}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class SendMessage extends SequentialBehaviour {

    public final AID marketAgent = new AID(MARKET_AGENT, AID.ISLOCALNAME);

    public final AtomicReference<Message> received = new AtomicReference<Message>(null);

    public SendMessage(final Agent agent, final Message toSend) {
        super(agent);

        try {
            final Action a = new Action(marketAgent, toSend);
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.addReceiver(new AID(MARKET_AGENT, AID.ISLOCALNAME));
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            myAgent.getContentManager().fillContent(aclMessage, a);
            addSubBehaviour(new SenderBehaviour(agent, aclMessage));
        }
        catch (Exception e) {
        }
    }

    private class SenderBehaviour extends AchieveREInitiator {

        public SenderBehaviour(final Agent agent, final ACLMessage aclMessage) {
            super(agent, aclMessage);
        }

        @Override
        public void handleInform(ACLMessage inform) {

            try {
                final ContentElement a = myAgent.getContentManager().extractContent(inform);

                if (!(a instanceof Action) || !(((Action) a).getAction() instanceof Message)) {
                    return;
                }

                received.set((Message) ((Action) a).getAction());
            }
            catch (Exception e) {
            }
        }

        @Override
        public void handleRefuse(ACLMessage aclMessage) {
            handleInform(aclMessage);
        }
    }
}
