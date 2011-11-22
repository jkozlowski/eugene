package eugene.market.esma.impl;

import eugene.market.esma.MatchingEngine;
import eugene.market.esma.Order;
import eugene.market.esma.OrderBook;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link MatchingEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MatchingEngineImpl implements MatchingEngine {

    @Override
    public MatchingResult match(@NotNull final OrderBook orderBook, @NotNull final Order buyOrder,
                                @NotNull final Order sellOrder) {

        checkNotNull(orderBook);
        checkNotNull(buyOrder);
        checkNotNull(sellOrder);

        // MARKET vs MARKET
        if (buyOrder.isOrdTypeMarket() && sellOrder.isOrdTypeMarket()) {

            // no lastMarketPrice
            if (Order.NO_PRICE.equals(orderBook.getLastMarketPrice())) {
                return MatchingResult.NO_MATCH;
            }
            else {
                return new MatchingResult(Match.YES, orderBook.getLastMarketPrice());
            }
        }

        if (buyOrder.isOrdTypeMarket() || sellOrder.isOrdTypeMarket()
            || (buyOrder.getPrice() >= sellOrder.getPrice())) {
            final Double price =
                    sellOrder.isOrdTypeLimit() ? sellOrder.getPrice() : buyOrder.getPrice();
            return new MatchingResult(Match.YES, price);
        }

        return MatchingResult.NO_MATCH;
    }
}
