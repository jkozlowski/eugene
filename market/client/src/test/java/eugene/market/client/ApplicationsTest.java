/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.client.impl.OrderBookApplication;
import eugene.market.client.impl.ProxyApplicationImpl;
import eugene.market.client.impl.TopOfBookPrinter;
import org.testng.annotations.Test;

import static eugene.market.client.Applications.orderBookApplication;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Applications.topOfBookPrinterApplication;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link Applications}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class ApplicationsTest {

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new Applications();
    }

    @Test
    public void testProxy() {
        assertThat(proxy(mock(Application.class)), is(ProxyApplicationImpl.class));
    }

    @Test
    public void testOrderBookApplication() {
        assertThat(orderBookApplication(mock(OrderBook.class)), is(OrderBookApplication.class));
    }
    
    @Test
    public void testTopOfBookPrinterApplication() {
        assertThat(topOfBookPrinterApplication(mock(OrderBook.class)), is(TopOfBookPrinter.class));
    }
}
