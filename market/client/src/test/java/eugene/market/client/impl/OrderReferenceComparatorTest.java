/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.OrderReference;
import org.testng.annotations.Test;

import static eugene.market.client.impl.OrderReferenceComparator.getInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link OrderReferenceComparator}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class OrderReferenceComparatorTest {


    @Test(expectedExceptions = NullPointerException.class)
    public void testCompareNullO1() {
        getInstance().compare(null, mock(OrderReference.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCompareNullO2() {
        getInstance().compare(mock(OrderReference.class), null);
    }

    @Test
    public void testCompareO1Earlier() {
        final OrderReference o1 = when(mock(OrderReference.class).getCreationTime()).thenReturn(1L).getMock();
        final OrderReference o2 = when(mock(OrderReference.class).getCreationTime()).thenReturn(2L).getMock();
        assertThat(getInstance().compare(o1, o2), is(lessThan(0)));
    }

    @Test
    public void testCompareO1Later() {
        final OrderReference o1 = when(mock(OrderReference.class).getCreationTime()).thenReturn(2L).getMock();
        final OrderReference o2 = when(mock(OrderReference.class).getCreationTime()).thenReturn(1L).getMock();
        assertThat(getInstance().compare(o1, o2), is(greaterThan(0)));
    }

    @Test
    public void testCompareCreatedTimeEqual() {
        final OrderReference o1 = when(mock(OrderReference.class).getCreationTime()).thenReturn(1L).getMock();
        final OrderReference o2 = when(mock(OrderReference.class).getCreationTime()).thenReturn(1L).getMock();
        assertThat(getInstance().compare(o1, o2), is(0));
    }
}
