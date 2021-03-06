/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
/**
 * Classes that represent messages that Agents can send in order to request Market Data updates and the Market to
 * in order to publish Market Data Updates.
 *
 * Message names and types are modelled after <code>BATS Multicast PITCH 4.8</code> and allow the Agents to receive
 * real-time full depth of book quotations and execution information. All orders and executions are anonymous,
 * and do not contain any member identity.
 *
 * <code>BATS Multicast PITCH 2.X</code> message types are prefixed with <code>BP</code> in order to eliminate confusion with
 * <code>FIX</code> messages, e.g. {@link AddOrder} message has message type <code>0x21</code>,
 * therefore in {@link MarketOntology} it has {@link AddOrder.TYPE} equal to <code>BP0x21</code>.
 *
 * In {@link MarketOntology}, <code>BATS Multicast PITCH 2.X</code> messages use the same Message format as <code>FIX</code>
 * messages. Therefore, the same field types are used when an alternative is available.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
package eugene.market.ontology.message.data;

import eugene.market.ontology.MarketOntology;
