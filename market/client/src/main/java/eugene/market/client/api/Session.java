package eugene.market.client.api;

import eugene.market.client.api.impl.Messages;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.Logon;
import jade.content.ContentManager;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Session} is a communication channel between the Agents and the Market Agent.
 *
 * {@link Session} sends an appropriate {@link Logon} message to the Market Agent in order to initiate the
 * communication channel. {@link Session} will route all {@link Message}s (sent through it
 * and received by it) to {@link Application}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class Session {

    private final Agent agent;

    private final AID marketAgent;

    private final Application application;

    private final String symbol;

    /**
     * Creates a {@link Session} that will route all messages to this <code>application</code> and will be executed by
     * this <code>agent</code>.
     *
     * @param agent       {@link Agent} that will execute this {@link Session}.
     * @param marketAgent {@link AID} of the {@link MarketAgent} to communicate with.
     * @param application implementation of {@link Application} that this {@link Session} will route {@link Message}s
     *                    to.
     * @param symbol      symbol handled by this {@link Session}.
     */
    public Session(final Agent agent, final AID marketAgent, final Application application, final String symbol) {
        checkNotNull(agent);
        checkNotNull(marketAgent);
        checkNotNull(application);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.agent = agent;
        this.marketAgent = marketAgent;
        this.application = application;
        this.symbol = symbol;
    }

    /**
     * Gets the {@link AID} of the {@link MarketAgent}.
     *
     * @return the {@link AID} of the {@link MarketAgent}.
     */
    // TODO: clone the AID just to be safe?
    public AID getMarketAgent() {
        return marketAgent;
    }

    /**
     * Gets the application.
     *
     * @return the application.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Gets the symbol.
     *
     * @return the symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the {@link Message} from this <code>aclMessage</code> <code>:content</code> slot.
     *
     * @param aclMessage {@link ACLMessage} to extract from.
     * @param type       type of {@link Message} expected.
     *
     * @return extracted {@link Message}, or <code>null</code> if <code>aclMessage</code> does not have a {@link
     *         Message} of type <code>type</code> in <code>:content</code> slot.
     */
    public <T extends Message> T extractMessage(final ACLMessage aclMessage, final Class<T> type) {
        return Messages.extractMessage(agent, aclMessage, type);
    }

    /**
     * Gets the {@link ACLMessage} with <code>:receiver</code> slot set to <code>to</code>, <code>:content</code>
     * slot set to <code>message</code> wrapped with {@link Action}, <code>:ontology</code> slot set to {@link
     * MarketOntology#getName()}, <code>:language</code> slot set to {@link MarketOntology#LANGUAGE} and {@link
     * ACLMessage#REQUEST} performative.
     *
     * @param message {@link Message} to set the <code>:content</code> slot to.
     *
     * @return {@link ACLMessage#REQUEST} message.
     *
     * @throws RuntimeException if {@link ContentManager#fillContent(ACLMessage, AbsContentElement)} throws {@link
     *                          CodecException} or {@link OntologyException}.
     */
    public ACLMessage aclRequest(final Message message) {
        return Messages.aclRequest(agent, marketAgent, message);
    }
}
