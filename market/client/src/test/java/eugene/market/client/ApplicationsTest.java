package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.client.impl.application.OrderBookApplication;
import eugene.market.client.impl.application.ProxyApplication;
import org.testng.annotations.Test;

import static eugene.market.client.Applications.orderBook;
import static eugene.market.client.Applications.proxy;
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
