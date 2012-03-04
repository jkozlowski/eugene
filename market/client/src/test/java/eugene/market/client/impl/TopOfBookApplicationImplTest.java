/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.client.TopOfBookApplication.Price;
import eugene.market.ontology.Defaults;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitPrice;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.client.TopOfBookApplication.NO_PRICE;
import static eugene.market.client.TopOfBookApplication.ReturnDefaultPrice.YES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link TopOfBookApplicationImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class TopOfBookApplicationImplTest {
    
    private static final Symbol defaultSymbol = Symbols.getSymbol(Defaults.defaultSymbol, Defaults.defaultTickSize,
                                                                  Defaults.defaultPrice);

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new TopOfBookApplicationImpl(null);
    }

    @Test
    public void testConstructor() {
        
        final TopOfBookApplication app = new TopOfBookApplicationImpl(defaultSymbol);
        assertThat(app.getLastPrice(Side.SELL), is(NO_PRICE));
        assertThat(app.getLastPrice(Side.BUY), is(NO_PRICE));
    }

    @Test
    public void testOnStart() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final Start start = mock(Start.class);
        final Agent agent = mock(Agent.class);

        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook, application);
        app.onStart(start, agent, session);
        verify(application).onStart(start, agent, session);
    }

    @Test
    public void testOnStop() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final Stop stop = mock(Stop.class);
        final Agent agent = mock(Agent.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.onStop(stop, agent, session);
        verify(application).onStop(stop, agent, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testToAppExecutionReport() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final ExecutionReport executionReport = mock(ExecutionReport.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.toApp(executionReport, session);
        verify(application).toApp(executionReport, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testToAppOrderCancelReject() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final OrderCancelReject orderCancelReject = mock(OrderCancelReject.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.toApp(orderCancelReject, session);
        verify(application).toApp(orderCancelReject, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testToAppAddOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final AddOrder addOrder = mock(AddOrder.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.toApp(addOrder, session);
        verify(application).toApp(addOrder, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testToAppDeleteOrder() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final DeleteOrder deleteOrder = mock(DeleteOrder.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.toApp(deleteOrder, session);
        verify(application).toApp(deleteOrder, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testToAppOrderExecuted() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final OrderExecuted orderExecuted = mock(OrderExecuted.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.toApp(orderExecuted, session);
        verify(application).toApp(orderExecuted, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testFromAppNewOrderSingle() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final NewOrderSingle newOrderSingle = mock(NewOrderSingle.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.fromApp(newOrderSingle, session);
        verify(application).fromApp(newOrderSingle, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test
    public void testFromAppOrderCancelRequest() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Session session = mock(Session.class);
        final Application application = mock(Application.class);
        final OrderCancelRequest orderCancelRequest = mock(OrderCancelRequest.class);

        final TopOfBookApplicationImpl app = getApp(orderBook, application, defaultSymbol);
        app.fromApp(orderCancelRequest, session);
        verify(application).fromApp(orderCancelRequest, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetPriceNullSide() {
        final TopOfBookApplication app = new TopOfBookApplicationImpl(defaultSymbol);
        app.getLastPrice(null, YES);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetPriceNullUseDefaultPrice() {
        final TopOfBookApplication app = new TopOfBookApplicationImpl(defaultSymbol);
        app.getLastPrice(Side.BUY, null);
    }
    
    @Test
    public void testGetPriceSellNoPriceReturnDefaultPriceNo() {
        final Order order = order(buy());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), defaultSymbol);
        assertThat(app.getLastPrice(Side.SELL), is(NO_PRICE));
    }

    @Test
    public void testGetPriceBuyNoPriceReturnDefaultPriceNo() {
        final Order order = order(sell());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.SELL)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), defaultSymbol);
        assertThat(app.getLastPrice(Side.BUY), is(NO_PRICE));
    }

    @Test
    public void testGetPriceSellNoPriceReturnDefaultPriceYes() {
        final Order order = order(buy());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), defaultSymbol);
        assertThat(app.getLastPrice(Side.SELL, YES).getPrice(), is(defaultSymbol.getDefaultPrice()));
        assertThat(app.getLastPrice(Side.SELL, YES).getSide(), is(Side.SELL));
    }

    @Test
    public void testGetPriceBuyNoPriceReturnDefaultPriceYes() {
        final Order order = order(sell());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.SELL)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), defaultSymbol);
        assertThat(app.getLastPrice(Side.BUY, YES).getPrice(), is(defaultSymbol.getDefaultPrice()));
        assertThat(app.getLastPrice(Side.BUY, YES).getSide(), is(Side.BUY));
    }

    @Test
    public void testUpdatePricesBuyChangedSellNotChanged() {
        final Order sell = order(limitPrice(sell(), BigDecimal.ONE));
        final Order buy = order(buy());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true).thenReturn(false);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(buy);
        when(orderBook.peek(Side.SELL)).thenReturn(sell);
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), defaultSymbol);

        assertThat(app.getLastPrice(Side.BUY), is(NO_PRICE));
        assertThat(app.getLastPrice(Side.SELL).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getLastPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getLastPrice(Side.SELL), is(PriceImpl.class));

        final Price sellPrice = app.getLastPrice(Side.SELL);

        app.updatePrices();

        assertThat(app.getLastPrice(Side.SELL), sameInstance(sellPrice));
        assertThat(app.getLastPrice(Side.BUY).getPrice(), is(buy.getPrice()));
        assertThat(app.getLastPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getLastPrice(Side.BUY), is(PriceImpl.class));
    }
    
    @Test
    public void testUpdatePricesBuyChangedToADifferentPriceSellNotChanged() {
        final Order sell = order(limitPrice(sell(), BigDecimal.ONE));
        final Order buy1 = order(buy());
        final Order buy2 = order(limitPrice(buy(), BigDecimal.ONE));
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(buy1).thenReturn(buy2);
        when(orderBook.peek(Side.SELL)).thenReturn(sell);
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), defaultSymbol);

        assertThat(app.getLastPrice(Side.BUY).getPrice(), is(buy1.getPrice()));
        assertThat(app.getLastPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getLastPrice(Side.BUY), is(PriceImpl.class));
        assertThat(app.getLastPrice(Side.SELL).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getLastPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getLastPrice(Side.SELL), is(PriceImpl.class));

        final Price sellPrice = app.getLastPrice(Side.SELL);

        app.updatePrices();

        assertThat(app.getLastPrice(Side.SELL), sameInstance(sellPrice));
        assertThat(app.getLastPrice(Side.BUY).getPrice(), is(buy2.getPrice()));
        assertThat(app.getLastPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getLastPrice(Side.BUY), is(PriceImpl.class));
    }

    @Test
    public void testUpdatePricesSellChangedBuyNotChanged() {
        final Order sell = order(sell());
        final Order buy = order(limitPrice(buy(), BigDecimal.ONE));
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false).thenReturn(false);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(buy);
        when(orderBook.peek(Side.SELL)).thenReturn(sell);
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), defaultSymbol);

        assertThat(app.getLastPrice(Side.SELL), is(NO_PRICE));
        assertThat(app.getLastPrice(Side.BUY).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getLastPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getLastPrice(Side.BUY), is(PriceImpl.class));

        final Price buyPrice = app.getLastPrice(Side.BUY);

        app.updatePrices();

        assertThat(app.getLastPrice(Side.BUY), sameInstance(buyPrice));
        assertThat(app.getLastPrice(Side.SELL).getPrice(), is(sell.getPrice()));
        assertThat(app.getLastPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getLastPrice(Side.SELL), is(PriceImpl.class));
    }

    @Test
    public void testUpdatePricesSellChangedToADifferentPriceBuyNotChanged() {
        final Order sell1 = order(limitPrice(sell(), BigDecimal.ONE));
        final Order sell2 = order(sell());
        final Order buy1 = order(buy());
        
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(buy1);
        when(orderBook.peek(Side.SELL)).thenReturn(sell1).thenReturn(sell2);
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), defaultSymbol);

        assertThat(app.getLastPrice(Side.BUY).getPrice(), is(buy1.getPrice()));
        assertThat(app.getLastPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getLastPrice(Side.BUY), is(PriceImpl.class));
        assertThat(app.getLastPrice(Side.SELL).getPrice(), is(sell1.getPrice()));
        assertThat(app.getLastPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getLastPrice(Side.SELL), is(PriceImpl.class));

        final Price buyPrice = app.getLastPrice(Side.BUY);

        app.updatePrices();

        assertThat(app.getLastPrice(Side.BUY), sameInstance(buyPrice));
        assertThat(app.getLastPrice(Side.SELL).getPrice(), is(sell2.getPrice()));
        assertThat(app.getLastPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getLastPrice(Side.SELL), is(PriceImpl.class));
    }
    
    @Test
    public void testIsEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Mockito.any(Side.class))).thenReturn(true);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        assertThat(app.isEmpty(Side.BUY), is(true));
        assertThat(app.isEmpty(Side.SELL), is(true));
        verify(orderBook).isEmpty(Side.BUY);
        verify(orderBook).isEmpty(Side.SELL);
    }
    
    @Test
    public void testHasBothSidesBothSidesNotEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Mockito.any(Side.class))).thenReturn(false);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        assertThat(app.hasBothSides(), is(true));
    }

    @Test
    public void testHasBothSidesSellEmptyBuyNotEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        assertThat(app.hasBothSides(), is(false));
    }

    @Test
    public void testHasBothSidesBuyEmptySellNotEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        assertThat(app.hasBothSides(), is(false));
    }

    @Test
    public void testHasBothSidesBothEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Mockito.any(Side.class))).thenReturn(true);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        assertThat(app.hasBothSides(), is(false));
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetSpreadHasBothSidesFalse() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Mockito.any(Side.class))).thenReturn(true);
        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(defaultSymbol, orderBook,
                                                                          mock(Application.class));
        app.getSpread();
    }
    
    @Test
    public void testGetSpread() {
        final Order sell = order(sell());
        final Order buy = order(limitPrice(buy(), new BigDecimal("99.999")));
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Mockito.any(Side.class))).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(buy);
        when(orderBook.peek(Side.SELL)).thenReturn(sell);
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), defaultSymbol);
        
        final BigDecimal expected = new BigDecimal("0.001");
        assertThat(app.getSpread(), is(expected));
    }

    /**
     * Gets a {@link TopOfBookApplicationImpl} initialized from this <code>application</code>.
     *
     * @param orderBook   initializing {@link OrderBook}.
     * @param application {@link Application} to pass to {@link TopOfBookApplication}.
     * @param symbol      {@link Symbol} to pass to {@link TopOfBookApplication}.
     *
     * @return initialized {@link TopOfBookApplicationImpl}.
     */
    public static TopOfBookApplicationImpl getApp(final OrderBook orderBook, final Application application,
                                                  final Symbol symbol) {
        final Session session = mock(Session.class);

        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(symbol, orderBook, application);
        app.onStart(mock(Start.class), mock(Agent.class), session);
        return app;
    }
}
