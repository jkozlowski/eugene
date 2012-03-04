/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.book.Order.NO_PRICE;
import static eugene.market.ontology.field.enums.Side.BUY;
import static eugene.market.ontology.field.enums.Side.SELL;

/**
 * Prints the top of the book to {@link System#out}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class TopOfBookPrinter extends ApplicationAdapter {

    public static final String PRINT_FORMAT = "%d,%f,%d,%f";

    private final OrderBook orderBook;

    private static final Logger LOG = LoggerFactory.getLogger(TopOfBookPrinter.class);

    /**
     * Creates a {@link TopOfBookPrinter} that will print this <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} to print.
     */
    public TopOfBookPrinter(final OrderBook orderBook) {
        checkNotNull(orderBook);
        this.orderBook = orderBook;
    }

    @Override
    public void toApp(final AddOrder addOrder, final Session session) {
        LOG.info(print());
    }

    @Override
    public void toApp(final DeleteOrder deleteOrder, final Session session) {
        LOG.info(print());
    }

    @Override
    public void toApp(final OrderExecuted orderExecuted, final Session session) {
        LOG.info(print());
    }

    @VisibleForTesting
    public String print() {
        final BigDecimal topBuy = orderBook.isEmpty(BUY) ? NO_PRICE : orderBook.peek(BUY).getPrice();
        final BigDecimal topAsk = orderBook.isEmpty(SELL) ? NO_PRICE : orderBook.peek(SELL).getPrice();

        long bidDepth = 0L;
        if (!orderBook.isEmpty(BUY)) {
            for (final Order order : orderBook.getBuyOrders()) {
                if (order.getPrice().equals(topBuy)) {
                    bidDepth += order.getOrderQty();
                }
                else {
                    break;
                }
            }
        }

        long askDepth = 0L;
        if (!orderBook.isEmpty(SELL)) {
            for (final Order order : orderBook.getSellOrders()) {
                if (order.getPrice().equals(topAsk)) {
                    askDepth += order.getOrderQty();
                }
                else {
                    break;
                }
            }
        }

        return String.format(PRINT_FORMAT, bidDepth, topBuy, askDepth, topAsk);
    }
}
