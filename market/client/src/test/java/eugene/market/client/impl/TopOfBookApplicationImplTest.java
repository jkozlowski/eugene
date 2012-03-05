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
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.Symbol;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitPrice;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.client.TopOfBookApplication.NO_PRICE;
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

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new TopOfBookApplicationImpl(null);
    }

    @Test
    public void testConstructor() {
        final TopOfBookApplication app = new TopOfBookApplicationImpl(mock(Symbol.class));
        assertThat(app.getPrice(Side.SELL), is(NO_PRICE));
        assertThat(app.getPrice(Side.BUY), is(NO_PRICE));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetPriceNullSide() {
        final TopOfBookApplication app = new TopOfBookApplicationImpl(mock(Symbol.class));
        app.getPrice(null);
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

        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(mock(Symbol.class), orderBook, application);
        app.onStart(start, agent, session);
        verify(application).onStart(start, agent, session);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testOnStartAlreadyStarted() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Application application = mock(Application.class);
        final Symbol symbol = mock(Symbol.class);
        final TopOfBookApplicationImpl app = getApp(orderBook, application, symbol);
        app.onStart(mock(Start.class), mock(Agent.class), mock(Session.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
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

        final TopOfBookApplicationImpl app = getApp(orderBook, application, mock(Symbol.class));
        app.fromApp(orderCancelRequest, session);
        verify(application).fromApp(orderCancelRequest, session);
        verify(orderBook, times(2)).isEmpty(Side.BUY);
        verify(orderBook, times(2)).isEmpty(Side.SELL);
        verifyNoMoreInteractions(orderBook);
    }
    
    @Test
    public void testGetPriceSellNoPrice() {
        final Order order = order(buy());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false);
        when(orderBook.peek(Side.BUY)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), mock(Symbol.class));
        assertThat(app.getPrice(Side.SELL), is(NO_PRICE));
    }

    @Test
    public void testGetPriceBuyNoPrice() {
        final Order order = order(sell());
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false);
        when(orderBook.peek(Side.SELL)).thenReturn(order);
        final TopOfBookApplication app = getApp(orderBook, mock(Application.class), mock(Symbol.class));
        assertThat(app.getPrice(Side.BUY), is(NO_PRICE));
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testUpdateNoSession() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(Side.SELL)).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(true);
        final Application application = mock(Application.class);

        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(mock(Symbol.class), orderBook, application);
        app.updatePrices();
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
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), mock(Symbol.class));

        assertThat(app.getPrice(Side.BUY), is(NO_PRICE));
        assertThat(app.getPrice(Side.SELL).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getPrice(Side.SELL), is(PriceImpl.class));

        final Price sellPrice = app.getPrice(Side.SELL);

        app.updatePrices();

        assertThat(app.getPrice(Side.SELL), sameInstance(sellPrice));
        assertThat(app.getPrice(Side.BUY).getPrice(), is(buy.getPrice()));
        assertThat(app.getPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getPrice(Side.BUY), is(PriceImpl.class));
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
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), mock(Symbol.class));

        assertThat(app.getPrice(Side.BUY).getPrice(), is(buy1.getPrice()));
        assertThat(app.getPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getPrice(Side.BUY), is(PriceImpl.class));
        assertThat(app.getPrice(Side.SELL).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getPrice(Side.SELL), is(PriceImpl.class));

        final Price sellPrice = app.getPrice(Side.SELL);

        app.updatePrices();

        assertThat(app.getPrice(Side.SELL), sameInstance(sellPrice));
        assertThat(app.getPrice(Side.BUY).getPrice(), is(buy2.getPrice()));
        assertThat(app.getPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getPrice(Side.BUY), is(PriceImpl.class));
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
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), mock(Symbol.class));

        assertThat(app.getPrice(Side.SELL), is(NO_PRICE));
        assertThat(app.getPrice(Side.BUY).getPrice(), is(BigDecimal.ONE));
        assertThat(app.getPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getPrice(Side.BUY), is(PriceImpl.class));

        final Price buyPrice = app.getPrice(Side.BUY);

        app.updatePrices();

        assertThat(app.getPrice(Side.BUY), sameInstance(buyPrice));
        assertThat(app.getPrice(Side.SELL).getPrice(), is(sell.getPrice()));
        assertThat(app.getPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getPrice(Side.SELL), is(PriceImpl.class));
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
        final TopOfBookApplicationImpl app = getApp(orderBook, mock(Application.class), mock(Symbol.class));

        assertThat(app.getPrice(Side.BUY).getPrice(), is(buy1.getPrice()));
        assertThat(app.getPrice(Side.BUY).getSide(), is(Side.BUY));
        assertThat(app.getPrice(Side.BUY), is(PriceImpl.class));
        assertThat(app.getPrice(Side.SELL).getPrice(), is(sell1.getPrice()));
        assertThat(app.getPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getPrice(Side.SELL), is(PriceImpl.class));

        final Price buyPrice = app.getPrice(Side.BUY);

        app.updatePrices();

        assertThat(app.getPrice(Side.BUY), sameInstance(buyPrice));
        assertThat(app.getPrice(Side.SELL).getPrice(), is(sell2.getPrice()));
        assertThat(app.getPrice(Side.SELL).getSide(), is(Side.SELL));
        assertThat(app.getPrice(Side.SELL), is(PriceImpl.class));
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
        final Simulation simulation = mock(Simulation.class);
        when(session.getSimulation()).thenReturn(simulation);
        when(simulation.getSymbol()).thenReturn(symbol);

        final TopOfBookApplicationImpl app = new TopOfBookApplicationImpl(mock(Symbol.class), orderBook, application);
        app.onStart(mock(Start.class), mock(Agent.class), session);
        return app;
    }
}
