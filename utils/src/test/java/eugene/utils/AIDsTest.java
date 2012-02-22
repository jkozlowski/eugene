/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils;

import jade.core.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static eugene.utils.AIDs.getAID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link AIDs}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
@PrepareForTest(Agent.class)
public class AIDsTest {

    private final String LOCAL_NAME = "localName";
    
    private final String HAP = "localhost:1099/JADE";

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new AIDs();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetAIDNullLocalName() {
        getAID(null, mock(Agent.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetAIDNullAgent() {
        getAID(LOCAL_NAME, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetAIDEmptyLocalName() {
        getAID("", mock(Agent.class));
    }
    
    @Test
    public void testGetAID() {
        final Agent agent = mock(Agent.class);
        when(agent.getHap()).thenReturn(HAP);
        assertThat(getAID(LOCAL_NAME, agent).getName(), is("localname@localhost:1099/jade"));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
