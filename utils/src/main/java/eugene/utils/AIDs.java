package eugene.utils;

import jade.core.AID;
import jade.core.Agent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility method for creating global {@link AID}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class AIDs {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This constructor should not be invoked.
     */
    public AIDs() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Gets the global {@link AID} for an agent with <code>localName</code> that resides in {@link Agent#getHap()} of
     * this <code>agent</code>.
     *
     * @param localName local name of an agent.
     * @param agent     the agent to get the Hap from.
     *
     * @return global {@link AID}.
     *
     * @throws NullPointerException     if any parameter is null.
     * @throws IllegalArgumentException if <code>localName</code> is empty.
     */
    public static AID getAID(final String localName, final Agent agent) {

        checkNotNull(localName);
        checkNotNull(agent);
        checkArgument(!localName.isEmpty());

        final StringBuilder b = new StringBuilder(localName.trim().toLowerCase());
        b.append("@");
        b.append(agent.getHap().toLowerCase());
        return new AID(b.toString(), AID.ISGUID);
    }
}
