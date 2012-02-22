/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.ontology;

import jade.content.AgentAction;

/**
 * {@link Stop} message is sent by the Simulation Agent to indicate that either:
 * <ul>
 * <li>Trader Agent's trading time has ended and the Trader Agent should cease sending messages to the Market
 * Agent and terminate.</li>
 * <li>The simulation has ended and therefore the recipient should terminate.</li>
 * </ul>
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class Stop implements AgentAction {
}
