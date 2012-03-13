/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
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

import static com.google.common.base.Optional.of;
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

    private final Optional<Price> SELL_DEFAULT_PRICE;

    private final Optional<Price> BUY_DEFAULT_PRICE;

    private final Application application;

    private final OrderBook orderBook;

    private Symbol symbol;

    private Optional<Price> buyPrice;

    private Optional<Price> sellPrice;

    /**
     * Default constructor.
     *
     * @param symbol symbol to use.
     */
    public TopOfBookApplicationImpl(final Symbol symbol) {
        this.symbol = checkNotNull(symbol);
        this.orderBook = defaultOrderBook();
        this.application = Applications.orderBookApplication(this.orderBook);
        this.buyPrice = Optional.absent();
        this.sellPrice = Optional.absent();
        this.SELL_DEFAULT_PRICE = Optional.<Price>of(new PriceImpl(symbol.getDefaultPrice(), Side.SELL, symbol));
        this.BUY_DEFAULT_PRICE = Optional.<Price>of(new PriceImpl(symbol.getDefaultPrice(), Side.BUY, symbol));
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
        this.orderBook = checkNotNull(orderBook);
        this.application = checkNotNull(application);
        this.symbol = checkNotNull(symbol);
        this.buyPrice = Optional.absent();
        this.sellPrice = Optional.absent();
        this.SELL_DEFAULT_PRICE = Optional.<Price>of(new PriceImpl(symbol.getDefaultPrice(), Side.SELL, symbol));
        this.BUY_DEFAULT_PRICE = Optional.<Price>of(new PriceImpl(symbol.getDefaultPrice(), Side.BUY, symbol));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Price> getLastPrice(final Side side) {
        return getLastPrice(side, ReturnDefaultPrice.NO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Price> getLastPrice(final Side side, final ReturnDefaultPrice returnDefaultPrice)
            throws NullPointerException {
        checkNotNull(side);
        checkNotNull(returnDefaultPrice);
        if (side.isBuy()) {
            return !buyPrice.isPresent() ?
                    (returnDefaultPrice.isYes() ? BUY_DEFAULT_PRICE : buyPrice) :
                    buyPrice;
        }
        else {
            return !sellPrice.isPresent() ?
                    (YES.equals(returnDefaultPrice) ? SELL_DEFAULT_PRICE : sellPrice) :
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
        final BigDecimal spread = orderBook.peek(Side.SELL).get().getPrice()
                                                                 .subtract(orderBook.peek(Side.BUY).get().getPrice());
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
            if (!buyPrice.isPresent()) {
                final Price newPrice = new PriceImpl(orderBook.peek(Side.BUY).get().getPrice(), Side.BUY, symbol);
                this.buyPrice = of(newPrice);
            }
            else if (!buyPrice.get().getPrice().equals(orderBook.peek(Side.BUY).get().getPrice())) {
                final Price newPrice = new PriceImpl(orderBook.peek(Side.BUY).get().getPrice(), Side.BUY, symbol);
                this.buyPrice = of(newPrice);
            }
        }

        if (!orderBook.isEmpty(Side.SELL)) {
            if (!sellPrice.isPresent()) {
                final Price newPrice = new PriceImpl(orderBook.peek(Side.SELL).get().getPrice(), Side.SELL, symbol);
                this.sellPrice = of(newPrice);
            }
            else if (!sellPrice.get().getPrice().equals(orderBook.peek(Side.SELL).get().getPrice())) {
                final Price newPrice = new PriceImpl(orderBook.peek(Side.SELL).get().getPrice(), Side.SELL, symbol);
                this.sellPrice = of(newPrice);
            }
        }
    }
}
