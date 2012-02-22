/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import static eugene.market.book.OrderBooks.readOnlyOrderBook;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests {@link ReadOnlyOrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class ReadOnlyOrderBookTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullDelegate() {
        readOnlyOrderBook(null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testInsert() {
        final OrderBook orderBook = readOnlyOrderBook(mock(OrderBook.class));
        orderBook.insert(null);
    }
    
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testInsertPartiallyExecuted() {
        final OrderBook orderBook = readOnlyOrderBook(mock(OrderBook.class));
        orderBook.insert(null, null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testCancel() {
        final OrderBook orderBook = readOnlyOrderBook(mock(OrderBook.class));
        orderBook.cancel(null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testExecute() {
        final OrderBook orderBook = readOnlyOrderBook(mock(OrderBook.class));
        orderBook.execute(null, null, null);
    }

    @Test
    public void testSize() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        orderBook.size(Side.BUY);
        verify(mock).size(Side.BUY);
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testIsEmpty() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        orderBook.isEmpty(Side.BUY);
        verify(mock).isEmpty(Side.BUY);
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testPeek() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        orderBook.peek(Side.BUY);
        verify(mock).peek(Side.BUY);
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testGetOrderStatus() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        final Order order = mock(Order.class);
        orderBook.getOrderStatus(order);
        verify(mock).getOrderStatus(order);
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testGetBuyOrders() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        orderBook.getBuyOrders();
        verify(mock).getBuyOrders();
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void testGetSellOrders() {
        final OrderBook mock = mock(OrderBook.class);
        final OrderBook orderBook = readOnlyOrderBook(mock);
        orderBook.getSellOrders();
        verify(mock).getSellOrders();
        verifyNoMoreInteractions(mock);
    }
}
