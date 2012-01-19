package eugene.market.client.api;

import eugene.market.book.OrderBook;
import eugene.market.client.api.impl.OrderBookApplication;
import eugene.market.client.api.impl.ProxyApplication;
import org.testng.annotations.Test;

import static eugene.market.client.api.Applications.orderBook;
import static eugene.market.client.api.Applications.proxy;
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

    @Test
    public void testProxy() {
        assertThat(proxy(mock(Application.class)), is(ProxyApplication.class));
    }

    @Test
    public void testOrderBook() {
        assertThat(orderBook(mock(OrderBook.class)), is(OrderBookApplication.class));
    }
}
