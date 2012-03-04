/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.book.Order;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Factory for creating {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class Messages {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public Messages() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Creates a {@link NewOrderSingle} message for a {@link OrdType#LIMIT} order.
     *
     * @param side   side of the order.
     * @param price  price of the order.
     * @param ordQty quantity of the order.
     *
     * @return message for this order.
     *
     * @throws NullPointerException     if any parameter is null.
     * @throws IllegalArgumentException if <code>price</code> is {@link Order#NO_PRICE}.
     * @throws IllegalArgumentException if <code>ordQty</code> is {@link Order#NO_QTY}.
     */
    public static NewOrderSingle newLimit(final Side side, final BigDecimal price, final Long ordQty) {

        checkNotNull(side);
        checkNotNull(price);
        checkNotNull(ordQty);
        checkArgument(price.compareTo(Order.NO_PRICE) > 0);
        checkArgument(ordQty.compareTo(Order.NO_QTY) > 0);

        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setOrdType(OrdType.LIMIT.field());
        newOrderSingle.setSide(side.field());
        newOrderSingle.setPrice(new Price(price));
        newOrderSingle.setOrderQty(new OrderQty(ordQty));

        return newOrderSingle;
    }
}
