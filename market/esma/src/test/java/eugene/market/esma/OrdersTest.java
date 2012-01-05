package eugene.market.esma;

import eugene.market.book.Order;
import eugene.market.ontology.message.data.AddOrder;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.esma.Orders.addOrder;
import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link Orders}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class OrdersTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddOrderNullOrder() {
        addOrder(null, defaultSymbol);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testAddOrderNullSymbol() {
        addOrder(mock(Order.class), null);
    }
    
    @Test
    public void testAddOrder() {
        final Order buy = order(buy());
        final AddOrder addOrder = addOrder(buy, defaultSymbol);
        assertThat(addOrder.getOrderID().getValue(), is(buy.getOrderID().toString()));
        assertThat(addOrder.getOrderQty().getValue(), is(buy.getOrderQty()));
        assertThat(addOrder.getPrice().getValue(), is(buy.getPrice()));
        assertThat(addOrder.getSide(), is(buy.getSide().field()));
        assertThat(addOrder.getSymbol().getValue(), is(defaultSymbol));
    }
}
