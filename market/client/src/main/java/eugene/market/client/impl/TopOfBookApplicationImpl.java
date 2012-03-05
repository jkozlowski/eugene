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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static eugene.market.book.OrderBooks.defaultOrderBook;

/**
 * Default implementation of {@link TopOfBookApplication}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class TopOfBookApplicationImpl implements TopOfBookApplication {

    private final Application application;

    private final OrderBook orderBook;

    private Session session;

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
        this.session = null;
        this.buyPrice = NO_PRICE;
        this.sellPrice = NO_PRICE;
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
        this.session = null;
        this.buyPrice = NO_PRICE;
        this.sellPrice = NO_PRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Price getPrice(final Side side) {
        checkNotNull(side);
        return side.isBuy() ? buyPrice : sellPrice;
    }

    @Override
    public void onStart(final Start start, final Agent agent, final Session session) {
        checkState(null == this.session);
        this.session = session;
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

        checkState(null != this.session);

        if (!orderBook.isEmpty(Side.BUY)) {
            if (buyPrice.equals(NO_PRICE)) {
                this.buyPrice = new PriceImpl(orderBook.peek(Side.BUY).getPrice(), Side.BUY,
                                              session.getSimulation().getSymbol());
            }
            else if (!buyPrice.getPrice().equals(orderBook.peek(Side.BUY).getPrice())) {
                this.buyPrice = new PriceImpl(orderBook.peek(Side.BUY).getPrice(), Side.BUY,
                                              session.getSimulation().getSymbol());
            }
        }

        if (!orderBook.isEmpty(Side.SELL)) {
            if (sellPrice.equals(NO_PRICE)) {
                this.sellPrice = new PriceImpl(orderBook.peek(Side.SELL).getPrice(), Side.SELL,
                                               session.getSimulation().getSymbol());
            }
            else if (!sellPrice.getPrice().equals(orderBook.peek(Side.SELL).getPrice())) {
                this.sellPrice = new PriceImpl(orderBook.peek(Side.SELL).getPrice(), Side.SELL,
                                               session.getSimulation().getSymbol());
            }
        }
    }
}
