/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.noise.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.OrderBook;
import eugene.market.client.OrderReference;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.client.TopOfBookApplication.ReturnDefaultPrice;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.simulation.agent.Symbol;
import jade.core.behaviours.TickerBehaviour;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.math.BigDecimal;
import java.util.Random;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.Messages.cancelRequest;
import static eugene.market.client.Messages.newLimit;
import static eugene.market.client.Messages.newMarket;
import static eugene.market.client.TopOfBookApplication.ReturnDefaultPrice.YES;

/**
 * Places either {@link Decision#MARKET}, {@link Decision#CANCEL} or {@link Decision#LIMIT_IN_SPREAD} or {@link
 * Decision#LIMIT_OUT_OF_SPREAD} order for a random {@link OrderQty} (between  to a random {@link Side},
 * and then sleeps for a random period of time.
 *
 * If there is no price on the {@link OrderBook}, {@link Symbol#getDefaultPrice()} will be used as current price.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviour extends TickerBehaviour {

    public static final double MARKET_PROB = 0.15D;

    public static final double CANCEL_PROB = MARKET_PROB + 0.50D;

    public static final double LIMIT_IN_SPREAD_PROB = 0.35D;

    public static final Integer MAX_SLEEP = 3000;

    private final Random generator;

    private final TopOfBookApplication topOfBook;

    private final Session session;

    private enum Decision {
        CANCEL,
        MARKET,
        LIMIT_IN_SPREAD,
        LIMIT_OUT_OF_SPREAD
    }

    private final RealDistribution decision = new UniformRealDistribution();

    private final RealDistribution ordSize = new LogNormalDistribution(4.5, 0.8);

    private final RealDistribution inSpreadPrice = new UniformRealDistribution();

    private final RealDistribution outOfSpreadPrice = new ExponentialDistribution(0.3D);

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will create an instance of {@link Random},
     * check this <code>topOfBook</code> for the current price and use this <code>session</code> to send orders.
     *
     * @param topOfBook {@link TopOfBookApplication} to check for the current price.
     * @param session   {@link Session} to use to send orders.
     */
    public PlaceOrderBehaviour(final TopOfBookApplication topOfBook, final Session session) {
        this(new Random(), topOfBook, session);
    }

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will use this <code>generator</code> to make decisions,
     * check this <code>topOfBook</code> for the current price and use this <code>session</code> to send orders.
     *
     * @param generator {@link Random} to use.
     * @param topOfBook {@link TopOfBookApplication} to check for the current price.
     * @param session   {@link Session} to use to send orders.
     */
    public PlaceOrderBehaviour(final Random generator, final TopOfBookApplication topOfBook, final Session session) {
        super(null, generator.nextInt(MAX_SLEEP) + 1);
        checkNotNull(generator);
        checkNotNull(topOfBook);
        checkNotNull(session);
        this.generator = generator;
        this.topOfBook = topOfBook;
        this.session = session;
    }

    @Override
    @VisibleForTesting
    public void onTick() {
        final Side side = (0 == generator.nextInt(2)) ? Side.BUY : Side.SELL;
        final int sleep = generator.nextInt(MAX_SLEEP) + 1;

        switch (getDecision()) {

            case MARKET:
                market(side);
                break;

            case CANCEL:
                cancel();
                break;

            case LIMIT_IN_SPREAD:
                limitInSpread(side);
                break;

            case LIMIT_OUT_OF_SPREAD:
                limitOutOfSpread(side);
                break;
        }

        super.reset(sleep);
    }

    /**
     * Cancels the oldest working order.
     */
    private void cancel() {
        final SortedSet<OrderReference> orders = session.getOrderReferences();
        if (!orders.isEmpty()) {
            session.send(cancelRequest(orders.first()));
        }
    }

    /**
     * Sends a {@link OrdType#MARKET} order for quantity matching the quantity on the {@link Side#getOpposite()} side
     * of the order book.
     *
     * @param side side of the order.
     */
    private void market(final Side side) {

        if (topOfBook.getOrderBook().isEmpty(side.getOpposite())) {
            return;
        }

        session.send(newMarket(side, topOfBook.getOrderBook().peek(side.getOpposite()).get().getOrderQty()));
    }

    /**
     * Sends a {@link OrdType#LIMIT} order between best ask and best bid.
     *
     * @param side side of the order.
     */
    private void limitInSpread(final Side side) {

        final Long ordQty = Double.valueOf(ordSize.sample()).longValue();

        if (!topOfBook.hasBothSides()) {
            session.send(newLimit(topOfBook.getLastPrice(side, YES).get(), ordQty));
            return;
        }

        final BigDecimal spread = topOfBook.getSpread();
        final BigDecimal additive = BigDecimal.valueOf(inSpreadPrice.sample()).multiply(spread);
        final BigDecimal price = topOfBook.getLastPrice(Side.BUY, ReturnDefaultPrice.NO).get().nextPrice(additive);
        session.send(newLimit(side, price, ordQty));
    }

    /**
     * Sends a {@link OrdType#LIMIT} order away from the current best price on this {@code side}.
     *
     * @param side side of the limit order.
     */
    private void limitOutOfSpread(final Side side) {

        final Long ordQty = Double.valueOf(ordSize.sample()).longValue();

        final BigDecimal priceMove = BigDecimal.valueOf(outOfSpreadPrice.sample());
        final BigDecimal price = topOfBook.getLastPrice(side, YES).get().prevPrice(priceMove);

        session.send(newLimit(side, price, ordQty));
    }

    private Decision getDecision() {

        final double choice = decision.sample();

        if (choice < MARKET_PROB) {
            return Decision.MARKET;
        }
        else if (choice < CANCEL_PROB) {
            return Decision.CANCEL;
        }
        else {

            final double limitType = decision.sample();
            if (limitType < LIMIT_IN_SPREAD_PROB) {
                return Decision.LIMIT_IN_SPREAD;
            }
            else {
                return Decision.LIMIT_OUT_OF_SPREAD;
            }
        }
    }
}
