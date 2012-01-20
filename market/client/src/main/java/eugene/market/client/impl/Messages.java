package eugene.market.client.impl;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods for converting between {@link ACLMessage}s and {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class Messages {

    private Messages() {
    }

    /**
     * Gets the {@link Message} from this <code>aclMessage</code> <code>:content</code> slot.
     *
     * @param agent      {@link Agent} to use to extract the {@link Message}.
     * @param aclMessage {@link ACLMessage} to extract from.
     * @param type       type of {@link Message} expected.
     *
     * @return extracted {@link Message}, or <code>null</code> if <code>aclMessage</code> does not have a {@link
     *         Message} of type <code>type</code> in <code>:content</code> slot.
     */
    public static <T extends Message> T extractMessage(final Agent agent, final ACLMessage aclMessage,
                                                       final Class<T> type) {

        checkNotNull(agent);
        checkNotNull(aclMessage);

        try {
            final ContentElement ce = agent.getContentManager().extractContent(aclMessage);

            if (ce instanceof Action &&
                    null != ((Action) ce).getAction() &&
                    (((Action) ce).getAction().getClass() == type)) {

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
     * Gets the {@link ACLMessage} with <code>:receiver</code> slot set to <code>to</code>, <code>:content</code>
     * slot set to <code>message</code> wrapped with {@link Action}, <code>:ontology</code> slot set to {@link
     * MarketOntology#getName()}, <code>:language</code> slot set to {@link MarketOntology#LANGUAGE} and {@link
     * ACLMessage#REQUEST} performative.
     *
     * @param agent   {@link Agent} to use to set the <code>:content</code> slot.
     * @param to      {@link AID} to set the <code>:receiver</code> slot to.
     * @param message {@link Message} to set the <code>:content</code> slot to.
     *
     * @return {@link ACLMessage#REQUEST} message.
     *
     * @throws RuntimeException if {@link ContentManager#fillContent(ACLMessage, AbsContentElement)} throws {@link
     *                          CodecException} or {@link OntologyException}.
     */
    public static ACLMessage aclRequest(final Agent agent, final AID to, final Message message) {

        checkNotNull(agent);
        checkNotNull(to);
        checkNotNull(message);

        try {
            final Action action = new Action(to, message);
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setOntology(MarketOntology.getInstance().getName());
            aclMessage.setLanguage(MarketOntology.LANGUAGE);
            aclMessage.addReceiver(to);

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
}
