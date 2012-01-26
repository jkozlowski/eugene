package eugene.market.esma.impl;

import eugene.market.book.Order;
import eugene.market.esma.impl.Orders.NewOrderSingleValidationException;
import eugene.market.esma.impl.execution.ExecutionEngine;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.message.NewOrderSingle;
import org.testng.annotations.Test;

import static eugene.market.esma.MockMessages.newOrderSingle;
import static eugene.market.esma.impl.Orders.newOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link Orders}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class OrdersTest {
    
    @Test(expectedExceptions = NewOrderSingleValidationException.class)
    public void testNewOrderNullExecutionEngine() throws NewOrderSingleValidationException {
        newOrder(null, mock(NewOrderSingle.class));
    }

    @Test(expectedExceptions = NewOrderSingleValidationException.class)
    public void testNewOrderNullNewOrderSingle() throws NewOrderSingleValidationException {
        newOrder(new ExecutionEngine(), null);
    }

    @Test
    public void testNewOrderNullPrice() throws NewOrderSingleValidationException {
        final NewOrderSingle newOrderSingle = newOrderSingle();
        newOrderSingle.setOrdType(OrdType.MARKET.field());
        newOrderSingle.setPrice(null);
        
        final ExecutionEngine executionEngine = new ExecutionEngine();
        
        final Order order = newOrder(executionEngine, newOrderSingle);
        assertThat(order.getOrderQty(), is(newOrderSingle.getOrderQty().getValue()));
        assertThat(order.getPrice(), is(Order.NO_PRICE));
        assertThat(order.getOrderID(), is(executionEngine.getOrderID() - 1L));
        assertThat(order.getOrdType(), is(OrdType.MARKET));
        assertThat(order.getSide().field(), is(newOrderSingle.getSide()));
    }
}
