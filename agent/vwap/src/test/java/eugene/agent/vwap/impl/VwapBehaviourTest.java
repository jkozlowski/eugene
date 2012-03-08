/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.agent.vwap.impl.state.State;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import org.testng.annotations.Test;

import java.util.Calendar;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link VwapBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullDeadline() {
        new VwapBehaviour(null, mock(VwapExecution.class), mock(State.class), mock(TopOfBookApplication.class),
                          mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullVwapExecution() {
        new VwapBehaviour(Calendar.getInstance(), null, mock(State.class), mock(TopOfBookApplication.class), mock(
                Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSendLimitState() {
        new VwapBehaviour(Calendar.getInstance(), mock(VwapExecution.class), null, mock(TopOfBookApplication.class),
                          mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new VwapBehaviour(Calendar.getInstance(), mock(VwapExecution.class), mock(State.class), null, mock(
                Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new VwapBehaviour(Calendar.getInstance(), mock(VwapExecution.class), mock(State.class), mock(
                TopOfBookApplication.class), null);
    }
}
