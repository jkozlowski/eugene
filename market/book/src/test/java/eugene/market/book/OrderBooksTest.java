/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book;

import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.book.impl.ReadOnlyOrderBook;
import org.testng.annotations.Test;

import static eugene.market.book.OrderBooks.defaultOrderBook;
import static eugene.market.book.OrderBooks.readOnlyOrderBook;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link OrderBooks}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class OrderBooksTest {

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new OrderBooks();
    }

    @Test
    public void testDefaultOrderBook() {
        assertThat(defaultOrderBook(), is(DefaultOrderBook.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testReadOnlyOrderBookNullDelegate() {
        readOnlyOrderBook(null);
    }

    @Test
    public void testReadOnlyOrderBook() {
        assertThat(readOnlyOrderBook(mock(OrderBook.class)), is(ReadOnlyOrderBook.class));
    }
}
