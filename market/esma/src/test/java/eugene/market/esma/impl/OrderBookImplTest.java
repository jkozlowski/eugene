package eugene.market.esma.impl;

import eugene.market.esma.MatchingEngine;
import eugene.market.esma.MatchingEngine.Match;
import eugene.market.esma.MatchingEngine.MatchingResult;
import eugene.market.esma.Order;
import eugene.market.esma.OrderBook;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

import static eugene.market.esma.impl.MockOrders.buy;
import static eugene.market.esma.impl.MockOrders.limitConstPriceEntryTime;
import static eugene.market.esma.impl.MockOrders.order;
import static eugene.market.esma.impl.MockOrders.sell;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link OrderBookImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderBookImplTest {

    public static final Double defaultLastMarketPrice = new Double(100.0D);

    public static final MatchingResult YES = new MatchingResult(Match.YES, defaultLastMarketPrice);

    public static final MatchingEngine matchingEngineMatchingResultYes =
            when(mock(MatchingEngine.class).match(any(OrderBook.class), any(Order.class), any(Order.class)))
                    .thenReturn(YES).getMock();

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMatchingEngine() {
        new OrderBookImpl(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullBuyOrders() {
        new OrderBookImpl(mock(MatchingEngine.class), null, mock(Queue.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSellOrders() {
        new OrderBookImpl(mock(MatchingEngine.class), mock(Queue.class), null);
    }

    @Test
    public void testNewBookEmpty() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertNullOrder() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        orderBook.insertOrder(null);
    }

    /**
     * Tests submitting a limit buy order to an empty book.
     */
    @Test
    public void testInsertLimitBuyToEmptyOrderBook() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        final Order buy = order(buy());
        assertThat(orderBook.insertOrder(buy), is(true));
        assertThat(orderBook.getBuyOrders().size(), is(1));
        assertThat(orderBook.getSellOrders().isEmpty(), is(true));
        assertThat(buy, isIn(orderBook.getBuyOrders()));
    }

    /**
     * Tests inserting a limit sell order to an empty book.
     */
    @Test
    public void testInsertLimitSellToEmptyOrderBook() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        final Order sell = order(sell());
        assertThat(orderBook.insertOrder(sell), is(true));
        assertThat(orderBook.getSellOrders().size(), is(1));
        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
        assertThat(sell, isIn(orderBook.getSellOrders()));
    }

    /**
     * Tests submitting a limit buy order to a book that already contains a limit buy order.
     */
    @Test
    public void testInsertLimitBuyToNonEmptyBook() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        final Order buy1 = order(limitConstPriceEntryTime(buy(), 100L));
        final Order buy2 = order(limitConstPriceEntryTime(buy(), 101L));
        assertThat(orderBook.insertOrder(buy1), is(true));
        assertThat(orderBook.insertOrder(buy2), is(true));
        final Collection<Order> orders = new ArrayList<Order>();
        orders.add(buy1);
        orders.add(buy2);
        assertThat(orderBook.getBuyOrders().size(), is(2));
        assertThat(orderBook.getSellOrders().isEmpty(), is(true));
        assertThat(orderBook.getBuyOrders(), hasItems(buy1, buy2));
    }

    /**
     * Tests submitting a limit sell order to a book that already contains a limit sell order.
     */
    @Test
    public void testInsertLimitSellToNonEmptyBook() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
        final Order sell1 = order(limitConstPriceEntryTime(sell(), 100L));
        final Order sell2 = order(limitConstPriceEntryTime(sell(), 101L));
        assertThat(orderBook.insertOrder(sell1), is(true));
        assertThat(orderBook.insertOrder(sell2), is(true));
        final Collection<Order> orders = new ArrayList<Order>();
        orders.add(sell1);
        orders.add(sell2);
        assertThat(orderBook.getSellOrders().size(), is(2));
        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
        assertThat(orderBook.getSellOrders(), hasItems(sell1, sell2));
    }

    /**
     * Tests submitting a matching order.
     */
    @Test
    public void testExecuteLimitMatchingTwoOrdersQuantityPrice() {
        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);

    }
//
//    /**
//     * Tests submitting a market buy order against a book that has two limit orders for matching quantity, i.e. the
//     * order will cause two executions.
//     */
//    @Test
//    public void testInsertMarketMatchingQuantity() {
//        final OrderBook orderBook = getOrderBookWithExistingHighLimitSellBuyAndLastMarketPrice();
//        final Order sell1 = order(limitOrdQtyPrice(sell(), 50L, 100.0D));
//        final Order sell2 = order(limitOrdQtyPrice(sell(), 50L, 100.0D));
//        final Order buy = order(orderQty(ordType(buy(), OrdType.MARKET), 100L));
//        assertThat(orderBook.insertOrder(sell1).isEmpty(), is(true));
//        assertThat(orderBook.insertOrder(sell2).isEmpty(), is(true));
//        final Set<Order> executions = orderBook.insertOrder(buy);
//        assertThat(orderBook.getSellOrders().size(), is(1));
//        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
//        assertThat(executions.size(), is(3));
//        assertThat(executions, hasItems(sell1, sell2, buy));
//        assertThat(sell1.isFilled(), is(true));
//        assertThat(sell2.isFilled(), is(true));
//        assertThat(buy.isFilled(), is(true));
//    }
//
//    /**
//     * Tests submitting a market buy order against a book that has two market orders for matching quantity, i.e. the
//     * order will cause two executions.
//     */
//    @Test
//    public void testInsertMarketExistingMarketMatchingQuantityExistingLastMarketPrice() {
//        final OrderBook orderBook = getOrderBookWithExistingHighLimitSellBuyAndLastMarketPrice();
//        final Order sell1 = order(orderQty(ordType(sell(), OrdType.MARKET), 50L));
//        final Order sell2 = order(orderQty(ordType(sell(), OrdType.MARKET), 50L));
//        final Order buy = order(orderQty(ordType(buy(), OrdType.MARKET), 100L));
//        assertThat(orderBook.insertOrder(sell1).isEmpty(), is(true));
//        assertThat(orderBook.insertOrder(sell2).isEmpty(), is(true));
//        final Set<Order> executions = orderBook.insertOrder(buy);
//        assertThat(orderBook.getSellOrders().size(), is(1));
//        assertThat(orderBook.getBuyOrders().isEmpty(), is(true));
//        assertThat(executions.size(), is(3));
//        assertThat(executions, hasItems(sell1, sell2, buy));
//        assertThat(sell1.isFilled(), is(true));
//        assertThat(sell2.isFilled(), is(true));
//        assertThat(buy.isFilled(), is(true));
//    }
//
//    @Test
//    public void testInsertLimitNotMatchingPrice() {
//        final OrderBook orderBook = getOrderBookWithExistingHighLimitSellBuyAndLastMarketPrice();
//        final Order buy = order(limitOrdQtyPrice(buy(), 50L, 99.9D));
//        assertThat(orderBook.insertOrder(buy).isEmpty(), is(true));
//        assertThat(orderBook.getSellOrders().size(), is(1));
//        assertThat(orderBook.getBuyOrders().size(), is(1));
//        assertThat(orderBook.getBuyOrders(), hasItems(buy));
//        assertThat(buy.isFilled(), is(false));
//    }
//
//    /**
//     * Gets {@link OrderBook} with {@link OrderBookImplTest#defaultLastMarketPrice}.
//     *
//     * @return {@link OrderBook} with {@link OrderBookImplTest#defaultLastMarketPrice}.
//     */
//    public OrderBook getOrderBookWithExistingHighLimitSellBuyAndLastMarketPrice() {
//        final OrderBook orderBook = new OrderBookImpl(matchingEngineMatchingResultYes);
//        final Order sell = order(limitOrdQtyPrice(sell(), 50L, 110.0D));
//        assertThat(orderBook.insertOrder(sell).isEmpty(), is(true));
//        orderBook.setLastMarketPrice(defaultLastMarketPrice);
//        return orderBook;
//    }
}
