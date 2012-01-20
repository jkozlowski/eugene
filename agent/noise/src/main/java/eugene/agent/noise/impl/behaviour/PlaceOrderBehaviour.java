package eugene.agent.noise.impl.behaviour;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.OrderBook;
import eugene.market.client.api.Session;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Places either {@link Strategy#AGGRESSIVE}, {@link Strategy#MID} or {@link Strategy#PASSIVE} order for a random
 * {@link OrderQty} (between {@link PlaceOrderBehaviour#MIN_ORDER_QTY} and {@link PlaceOrderBehaviour#MAX_ORDER_QTY})
 * and to a random {@link Side}, and then sleeps for a noise period of time.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviour extends TickerBehaviour {

    public static final int MAX_SPREAD = 10;

    public static final int MIN_SPREAD = 1;

    public static final double DEFAULT_TICK = 1.0D;

    public static final double DEFAULT_CURRENT_PRICE = 100.0D;

    public static final Integer MAX_SLEEP = 6000;

    public static final int MAX_ORDER_QTY = 3000;

    public static final int MIN_ORDER_QTY = 1000;

    private final Random generator = new Random();

    private final OrderBook orderBook;

    private final Session session;

    private enum Strategy {

        /**
         * Sends a {@link OrdType#LIMIT} order with a price that is one tick above the current best price.
         */
        AGGRESSIVE,

        /**
         * Sends a {@link OrdType#LIMIT} order at the current best price.
         */
        MID,

        /**
         * Sends a {@link OrdType#LIMIT} order a random number ticks away from the current best price.
         */
        PASSIVE
    }

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will be executed by this <code>agent</code>
     *
     * @param agent     {@link Agent} that will execute this {@link PlaceOrderBehaviour}.
     * @param orderBook {@link OrderBook} to check for the current price.
     */
    public PlaceOrderBehaviour(final Agent agent, final OrderBook orderBook, final Session session) {
        super(agent, new Random().nextInt(MAX_SLEEP));
        checkNotNull(agent);
        checkNotNull(orderBook);
        checkNotNull(session);
        this.orderBook = orderBook;
        this.session = session;
    }

    @Override
    @VisibleForTesting
    public void onTick() {

        final Strategy strategy = getStrategy();
        final Side side = (0 == generator.nextInt(1)) ? Side.BUY : Side.SELL;
        final Long orderQty = getOrderQty();
        final int sleep = generator.nextInt(MAX_SLEEP);

        super.reset(sleep);
    }

    /**
     * Gets a random {@link Strategy}.
     *
     * @return random {@link Strategy}.
     */
    private Strategy getStrategy() {
        final int random = generator.nextInt(2);
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
        return new Long(MIN_ORDER_QTY + additive);
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
                if (side.isBuy()) {
                    newOrderSingle.setPrice(new Price(curPrice + DEFAULT_TICK));
                }
                else {
                    newOrderSingle.setPrice(new Price(curPrice - DEFAULT_TICK));
                }
                break;

            case MID:
                newOrderSingle.setPrice(new Price(curPrice));
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
