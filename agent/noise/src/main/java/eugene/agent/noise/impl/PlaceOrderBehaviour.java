package eugene.agent.noise.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.OrderBook;
import eugene.market.client.Session;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import jade.core.behaviours.TickerBehaviour;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Places either {@link Strategy#AGGRESSIVE}, {@link Strategy#MID} or {@link Strategy#PASSIVE} order for a random
 * {@link OrderQty} (between {@link PlaceOrderBehaviour#MIN_ORDER_QTY} and {@link PlaceOrderBehaviour#MAX_ORDER_QTY})
 * and to a random {@link Side}, and then sleeps for a random period of time.
 *
 * If there is no price on the {@link OrderBook}, {@link PlaceOrderBehaviour#DEFAULT_CURRENT_PRICE} will be used as
 * current price.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviour extends TickerBehaviour {

    public static final int MAX_SPREAD = 10;

    public static final int MIN_SPREAD = 1;

    public static final double DEFAULT_TICK = 1.0D;

    public static final double DEFAULT_CURRENT_PRICE = 100.0D;

    public static final Integer MAX_SLEEP = 500;

    public static final int MAX_ORDER_QTY = 3000;

    public static final int MIN_ORDER_QTY = 1000;

    private final Random generator;

    private final OrderBook orderBook;

    private final Session session;

    private enum Strategy {

        /**
         * Sends a {@link OrdType#MARKET}.
         */
        AGGRESSIVE,

        /**
         * Sends a {@link OrdType#LIMIT} order with a price that is one tick better than the the current best price.
         */
        MID,

        /**
         * Sends a {@link OrdType#LIMIT} order a random number ticks away from the current best price.
         */
        PASSIVE
    }

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will create an instance of {@link Random},
     * check this <code>orderBook</code> for the current price and use this <code>session</code> to send orders.
     *
     * @param orderBook {@link OrderBook} to check for the current price.
     * @param session   {@link Session} to use to send orders.
     */
    public PlaceOrderBehaviour(final OrderBook orderBook, final Session session) {
        this(new Random(), orderBook, session);
    }

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will use this <code>generator</code> to make decisions,
     * check this <code>orderBook</code> for the current price and use this <code>session</code> to send orders.
     *
     * @param generator {@link Random} to use.
     * @param orderBook {@link OrderBook} to check for the current price.
     * @param session   {@link Session} to use to send orders.
     */
    public PlaceOrderBehaviour(final Random generator, final OrderBook orderBook, final Session session) {
        super(null, generator.nextInt(MAX_SLEEP) + 1);
        checkNotNull(generator);
        checkNotNull(orderBook);
        checkNotNull(session);
        this.generator = generator;
        this.orderBook = orderBook;
        this.session = session;
    }

    @Override
    @VisibleForTesting
    public void onTick() {
        final Strategy strategy = getStrategy();
        final Side side = (0 == generator.nextInt(2)) ? Side.BUY : Side.SELL;
        final Long orderQty = getOrderQty();
        final int sleep = generator.nextInt(MAX_SLEEP) + 1;

        session.send(newOrderSingle(strategy, side, orderQty));

        super.reset(sleep);
    }

    /**
     * Gets a random {@link Strategy}.
     *
     * @return random {@link Strategy}.
     */
    private Strategy getStrategy() {
        final int random = generator.nextInt(3);
        return 0 == random ? Strategy.AGGRESSIVE : (1 == random ? Strategy.MID : Strategy.PASSIVE);
    }

    /**
     * Gets a random orderQty between {@link PlaceOrderBehaviour#MIN_ORDER_QTY} and {@link
     * PlaceOrderBehaviour#MAX_ORDER_QTY}.
     *
     * @return random orderQty.
     */
    private Long getOrderQty() {
        final int additive = generator.nextInt(MAX_ORDER_QTY - MIN_ORDER_QTY);
        return Long.valueOf(MIN_ORDER_QTY + additive);
    }

    /**
     * Gets a {@link NewOrderSingle} that will create an order for this <code>strategy</code>,
     * <code>side</code> and <code>orderQty</code>.
     *
     * @param strategy type of order to send.
     * @param side     {@link Side} of the order.
     * @param orderQty quantity of the order.
     *
     * @return {@link NewOrderSingle} that will create an order for these parameters.
     */
    private NewOrderSingle newOrderSingle(final Strategy strategy, final Side side, Long orderQty) {

        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setOrderQty(new OrderQty(orderQty));
        newOrderSingle.setSide(side.field());
        newOrderSingle.setOrdType(OrdType.LIMIT.field());

        final double curPrice = orderBook.isEmpty(side) ? DEFAULT_CURRENT_PRICE : orderBook.peek(side).getPrice();

        switch (strategy) {

            case AGGRESSIVE:
                newOrderSingle.setOrdType(OrdType.MARKET.field());
                break;

            case MID:
                if (side.isBuy()) {
                    newOrderSingle.setPrice(new Price(curPrice + DEFAULT_TICK));
                }
                else {
                    newOrderSingle.setPrice(new Price(curPrice - DEFAULT_TICK));
                }
                break;

            case PASSIVE:
                final int additive = generator.nextInt(MAX_SPREAD - MIN_SPREAD);
                if (side.isBuy()) {
                    newOrderSingle.setPrice(new Price(curPrice - additive * DEFAULT_TICK));
                }
                else {
                    newOrderSingle.setPrice(new Price(curPrice + additive * DEFAULT_TICK));
                }
                break;
        }

        return newOrderSingle;
    }
}
