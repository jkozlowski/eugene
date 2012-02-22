/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.esma.impl;

import eugene.market.esma.impl.Repository.Tuple;
import jade.lang.acl.ACLMessage;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link Repository.Tuple}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class TupleTest {
    
    public static final String clOrdID = "order01";
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAID() {
        new Tuple(null, clOrdID);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullClOrdID() {
        new Tuple(mock(ACLMessage.class), null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyClOrdID() {
        new Tuple(mock(ACLMessage.class), "");
    }
}
