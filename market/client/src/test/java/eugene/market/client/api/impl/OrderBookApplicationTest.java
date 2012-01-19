package eugene.market.client.api.impl;

import eugene.market.book.DefaultOrderBook;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.client.api.Session;
import eugene.market.client.api.impl.OrderBookApplication;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.TradeID;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultOrderID;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultTradeID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests {@link OrderBookApplication}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
@PrepareForTest({Session.class})
public class OrderBookApplicationTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new OrderBookApplication(null);
    }

    @Test
    public void testConstructorEmptyOrderMap() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);
        assertThat(application.getOrderMap().isEmpty(), is(true));
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testToAppAddOrderNotValidOrderID() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID("not-a-number"));

        application.toApp(addOrder, mock(Session.class));
    }

    @Test
    public void testToAppAddOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(defaultOrderID));
        addOrder.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder.setPrice(new Price(defaultPrice));
        addOrder.setSide(Side.BUY.field());

        application.toApp(addOrder, mock(Session.class));

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderBook).insertOrder(captor.capture());

        assertThat(captor.getValue().getOrderID(), is(Long.parseLong(defaultOrderID)));
        assertThat(captor.getValue().getOrderQty(), is(defaultOrdQty));
        assertThat(captor.getValue().getOrdType(), is(OrdType.LIMIT));
        assertThat(captor.getValue().getPrice(), is(defaultPrice));
        assertThat(captor.getValue().getSide(), is(Side.BUY));
        assertThat(application.getOrderMap().get(defaultOrderID), is(captor.getValue()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testToAppDeleteOrderUnknownOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.setOrderID(new OrderID(defaultOrderID));

        application.toApp(deleteOrder, mock(Session.class));
    }

    @Test
    public void testToAppDeleteOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(defaultOrderID));
        addOrder.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder.setPrice(new Price(defaultPrice));
        addOrder.setSide(Side.BUY.field());

        application.toApp(addOrder, mock(Session.class));

        final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderBook).insertOrder(captor.capture());

        final DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.setOrderID(new OrderID(defaultOrderID));

        application.toApp(deleteOrder, mock(Session.class));

        verify(orderBook).cancel(captor.getValue());
        assertThat(application.getOrderMap().containsValue(captor.getValue()), is(false));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testToAppOrderExecutedUnknownOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setOrderID(new OrderID(defaultOrderID));
        orderExecuted.setLastPx(new LastPx(defaultPrice));
        orderExecuted.setLastQty(new LastQty(defaultOrdQty));
        orderExecuted.setLeavesQty(new LeavesQty(defaultOrdQty));
        orderExecuted.setTradeID(new TradeID(defaultTradeID));

        application.toApp(orderExecuted, mock(Session.class));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testToAppOrderExecutedOrderNotAtTopOfOrderBook() {
        final OrderBook orderBook = new DefaultOrderBook();
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder1 = new AddOrder();
        addOrder1.setOrderID(new OrderID(defaultOrderID));
        addOrder1.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder1.setPrice(new Price(defaultPrice));
        addOrder1.setSide(Side.BUY.field());

        application.toApp(addOrder1, mock(Session.class));

        final AddOrder addOrder2 = new AddOrder();
        addOrder2.setOrderID(new OrderID(defaultOrderID + "1"));
        addOrder2.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder2.setPrice(new Price(defaultPrice + 1L));
        addOrder2.setSide(Side.BUY.field());

        application.toApp(addOrder2, mock(Session.class));

        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setOrderID(new OrderID(defaultOrderID));
        orderExecuted.setLastPx(new LastPx(defaultPrice));
        orderExecuted.setLastQty(new LastQty(defaultOrdQty));
        orderExecuted.setLeavesQty(new LeavesQty(defaultOrdQty));
        orderExecuted.setTradeID(new TradeID(defaultTradeID));

        application.toApp(orderExecuted, mock(Session.class));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testToAppOrderExecutedLeavesQtyDoesNotMatch() {
        final OrderBook orderBook = new DefaultOrderBook();
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder1 = new AddOrder();
        addOrder1.setOrderID(new OrderID(defaultOrderID));
        addOrder1.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder1.setPrice(new Price(defaultPrice));
        addOrder1.setSide(Side.BUY.field());

        application.toApp(addOrder1, mock(Session.class));

        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setOrderID(new OrderID(defaultOrderID));
        orderExecuted.setLastPx(new LastPx(defaultPrice));
        orderExecuted.setLastQty(new LastQty(defaultOrdQty - 1L));
        orderExecuted.setLeavesQty(new LeavesQty(2L));
        orderExecuted.setTradeID(new TradeID(defaultTradeID));

        application.toApp(orderExecuted, mock(Session.class));
    }

    @Test
    public void testToAppOrderExecuted() {
        final OrderBook orderBook = new DefaultOrderBook();
        final OrderBookApplication application = new OrderBookApplication(orderBook);

        final AddOrder addOrder1 = new AddOrder();
        addOrder1.setOrderID(new OrderID(defaultOrderID));
        addOrder1.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder1.setPrice(new Price(defaultPrice));
        addOrder1.setSide(Side.BUY.field());

        application.toApp(addOrder1, mock(Session.class));

        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setOrderID(new OrderID(defaultOrderID));
        orderExecuted.setLastPx(new LastPx(defaultPrice));
        orderExecuted.setLastQty(new LastQty(defaultOrdQty - 1L));
        orderExecuted.setLeavesQty(new LeavesQty(1L));
        orderExecuted.setTradeID(new TradeID(defaultTradeID));

        application.toApp(orderExecuted, mock(Session.class));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
