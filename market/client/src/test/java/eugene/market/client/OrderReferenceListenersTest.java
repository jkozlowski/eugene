package eugene.market.client;

import eugene.market.client.impl.OrderReferenceListenerProxyImpl;
import org.testng.annotations.Test;

import static eugene.market.client.OrderReferenceListeners.proxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link OrderReferenceListeners}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OrderReferenceListenersTest {

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new OrderReferenceListeners();
    }

    @Test
    public void testProxy() {
        assertThat(proxy(OrderReferenceListener.EMPTY_LISTENER), is(OrderReferenceListenerProxyImpl.class));
    }
}
