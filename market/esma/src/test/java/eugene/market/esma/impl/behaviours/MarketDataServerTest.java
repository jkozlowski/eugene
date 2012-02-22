/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.esma.impl.behaviours;

import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import jade.core.Agent;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link MarketDataServer}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataServerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new MarketDataServer(null, mock(MarketDataEngine.class), mock(Repository.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketDataEngine() {
        new MarketDataServer(mock(Agent.class), null, mock(Repository.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullRepository() {
        new MarketDataServer(mock(Agent.class), mock(MarketDataEngine.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new MarketDataServer(mock(Agent.class), mock(MarketDataEngine.class), mock(Repository.class), null);
    }
}
