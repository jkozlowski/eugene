/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import org.testng.annotations.Test;

import java.util.TreeMap;

import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Message}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class MessageTest {
    
    private static final class TestMessage extends Message {

        @Override
        public String getType() {
            return "-1";
        }
    }
    
    @Test
    public void testEqualsSameInstance() {
        final TestMessage testMessage = new TestMessage();
        assertThat(testMessage.equals(testMessage), is(true));
    }

    @Test
    public void testEqualsDifferentClass() {
        assertThat(new TestMessage().equals(new Object()), is(false));
    }

    @Test
    public void testEqualsNullObject() {
        assertThat(new TestMessage().equals(null), is(false));
    }
    
    @Test
    public void testHashCode() {
        final TestMessage testMessage = new TestMessage();
        assertThat(testMessage.hashCode(), is(new TreeMap<Integer, Field<?>>().hashCode()));
    }

    @Test
    public void testToString() {
        final TestMessage testMessage = new TestMessage();
        testMessage.setField(Side.TAGi, new Side(Side.BUY));
        testMessage.setField(Price.TAGi, new Price(defaultPrice));
        assertThat(testMessage.toString(), is("TestMessage[Price=" + defaultPrice + ", Side=1]"));
    }
}
