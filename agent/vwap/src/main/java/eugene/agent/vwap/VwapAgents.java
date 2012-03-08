/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.agent.vwap;

import eugene.agent.vwap.impl.VwapAgent;
import eugene.agent.vwap.impl.state.SendLimitBestPriceCorrectSide;
import eugene.agent.vwap.impl.state.SendLimitBestPriceWrongSide;
import jade.core.Agent;

/**
 * Factory for agents that implement the VWAP Algorithm.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public final class VwapAgents {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public VwapAgents() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Gets an {@link Agent} that implements a correct VWAP Algorithm, that sends orders based on prices on the side
     * of the {@link VwapExecution}.
     *
     * @param vwapExecution execution plan.
     *
     * @return an agent that implements a correct VWAP Algorithm.
     *
     * @throws NullPointerException if {@code vwapExecution} is null.
     */
    public static Agent vwapNoError(final VwapExecution vwapExecution) throws NullPointerException {
        return new VwapAgent(vwapExecution, new SendLimitBestPriceCorrectSide());
    }

    /**
     * Gets an {@link Agent} that implements an incorrect VWAP Algorithm, that sends orders based on prices on the
     * opposite side of the {@link VwapExecution}.
     *
     * @param vwapExecution execution plan.
     *
     * @return an agent that implements an incorrect VWAP Algorithm.
     *
     * @throws NullPointerException if {@code vwapExecution} is null.
     */
    public static Agent vwapError(final VwapExecution vwapExecution) throws NullPointerException {
        return new VwapAgent(vwapExecution, new SendLimitBestPriceWrongSide());
    }
}
