/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.OrderBook;
import eugene.market.client.Application;
import eugene.market.client.Applications;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.agent.Symbol;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static eugene.market.book.OrderBooks.defaultOrderBook;
import static eugene.market.book.OrderBooks.readOnlyOrderBook;
import static eugene.market.client.TopOfBookApplication.ReturnDefaultPrice.YES;

/**
 * Default implementation of {@link TopOfBookApplication}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class TopOfBookApplicationImpl implements TopOfBookApplication {

    private final Price SELL_DEFAULT_PRICE;

    private final Price BUY_DEFAULT_PRICE;

    private final Application application;

    private final OrderBook orderBook;

    private Symbol symbol;

    private Price buyPrice;

    private Price sellPrice;

    /**
     * Default constructor.
     *
     * @param symbol symbol to use.
     */
    public TopOfBookApplicationImpl(final Symbol symbol) {
        checkNotNull(symbol);
        this.orderBook = defaultOrderBook();
        this.application = Applications.orderBookApplication(this.orderBook);
        this.symbol = symbol;
        this.buyPrice = NO_PRICE;
        this.sellPrice = NO_PRICE;
        this.SELL_DEFAULT_PRICE = new PriceImpl(symbol.getDefaultPrice(), Side.SELL, symbol);
        this.BUY_DEFAULT_PRICE = new PriceImpl(symbol.getDefaultPrice(), Side.BUY, symbol);
    }

    /**
     * Constructor used for testing.
     *
     * @param symbol      symbol to use.
     * @param orderBook   {@link OrderBook} to use.
     * @param application {@link Application} that should be updating this <code>orderBook</code>.
     */
    @VisibleForTesting
    public TopOfBookApplicationImpl(final Symbol symbol, final OrderBook orderBook, final Application application) {
        checkNotNull(symbol);
        checkNotNull(orderBook);
        checkNotNull(application);
        this.orderBook = orderBook;
        this.application = application;
        this.symbol = symbol;
        this.buyPrice = NO_PRICE;
        this.sellPrice = NO_PRICE;
        this.SELL_DEFAULT_PRICE = new PriceImpl(symbol.getDefaultPrice(), Side.SELL, symbol);
        this.BUY_DEFAULT_PRICE = new PriceImpl(symbol.getDefaultPrice(), Side.BUY, symbol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Price getLastPrice(final Side side) {
        return getLastPrice(side, ReturnDefaultPrice.NO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Price getLastPrice(final Side side, final ReturnDefaultPrice returnDefaultPrice)
            throws NullPointerException {
        checkNotNull(side);
        checkNotNull(returnDefaultPrice);
        if (side.isBuy()) {
            return NO_PRICE.equals(buyPrice) ?
                    (YES.equals(returnDefaultPrice) ? BUY_DEFAULT_PRICE : NO_PRICE) :
                    buyPrice;
        }
        else {
            return NO_PRICE.equals(sellPrice) ?
                    (YES.equals(returnDefaultPrice) ? SELL_DEFAULT_PRICE : NO_PRICE) :
                    sellPrice;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty(final Side side) {
        return orderBook.isEmpty(side);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBothSides() {
        return !orderBook.isEmpty(Side.BUY) && !orderBook.isEmpty(Side.SELL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getSpread() {
        checkState(hasBothSides());
        final int scale = symbol.getTickSize().scale();
        final BigDecimal spread = orderBook.peek(Side.SELL).getPrice().subtract(orderBook.peek(Side.BUY).getPrice());
        return spread.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderBook getOrderBook() {
        return readOnlyOrderBook(orderBook);
    }

    @Override
    public void onStart(final Start start, final Agent agent, final Session session) {
        application.onStart(start, agent, session);
        updatePrices();
    }

    @Override
    public void onStop(Stop stop, Agent agent, Session session) {
        application.onStop(stop, agent, session);
        updatePrices();
    }

    @Override
    public void toApp(ExecutionReport executionReport, Session session) {
        application.toApp(executionReport, session);
        updatePrices();
    }

    @Override
    public void toApp(OrderCancelReject orderCancelReject, Session session) {
        application.toApp(orderCancelReject, session);
        updatePrices();
    }

    @Override
    public void toApp(AddOrder addOrder, Session session) {
        application.toApp(addOrder, session);
        updatePrices();
    }

    @Override
    public void toApp(DeleteOrder deleteOrder, Session session) {
        application.toApp(deleteOrder, session);
        updatePrices();
    }

    @Override
    public void toApp(OrderExecuted orderExecuted, Session session) {
        application.toApp(orderExecuted, session);
        updatePrices();
    }

    @Override
    public void fromApp(NewOrderSingle newOrderSingle, Session session) {
        application.fromApp(newOrderSingle, session);
        updatePrices();
    }

    @Override
    public void fromApp(OrderCancelRequest orderCancelRequest, Session session) {
        application.fromApp(orderCancelRequest, session);
        updatePrices();
    }

    /**
     * Updated {@link #buyPrice} and {@link #sellPrice}.
     */
    @VisibleForTesting
    void updatePrices() {

        if (!orderBook.isEmpty(Side.BUY)) {
            if (buyPrice.equals(NO_PRICE)) {
                this.buyPrice = new PriceImpl(orderBook.peek(Side.BUY).getPrice(), Side.BUY,
                                              symbol);
            }
            else if (!buyPrice.getPrice().equals(orderBook.peek(Side.BUY).getPrice())) {
                this.buyPrice = new PriceImpl(orderBook.peek(Side.BUY).getPrice(), Side.BUY, symbol);
            }
        }

        if (!orderBook.isEmpty(Side.SELL)) {
            if (sellPrice.equals(NO_PRICE)) {
                this.sellPrice = new PriceImpl(orderBook.peek(Side.SELL).getPrice(), Side.SELL, symbol);
            }
            else if (!sellPrice.getPrice().equals(orderBook.peek(Side.SELL).getPrice())) {
                this.sellPrice = new PriceImpl(orderBook.peek(Side.SELL).getPrice(), Side.SELL, symbol);
            }
        }
    }
}
