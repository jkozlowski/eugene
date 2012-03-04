/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.book.Order;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;

/**
 * Reference to an order submitted in a {@link Session} that can be used to cancel the order and check execution
 * status. {@link OrderReference} is updated by the {@link Session} until it is filled or cancelled.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface OrderReference {

    /**
     * Gets the clOrdID.
     *
     * @return the clOrdID.
     */
    String getClOrdID();

    /**
     * Gets the creationTime.
     *
     * @return the creationTime.
     */
    Long getCreationTime();

    /**
     * Gets the ordStatus.
     *
     * @return the ordStatus.
     */
    OrdStatus getOrdStatus();

    /**
     * Gets the side.
     *
     * @return the side.
     */
    Side getSide();

    /**
     * Gets the ordType.
     *
     * @return the ordType.
     */
    OrdType getOrdType();

    /**
     * Gets the price.
     *
     * @return the price or {@link Order#NO_PRICE} if the order is of type {@link OrdType#MARKET}.
     */
    BigDecimal getPrice();

    /**
     * Gets the orderQty.
     *
     * @return the orderQty.
     */
    Long getOrderQty();

    /**
     * Gets the avgPx.
     *
     * @return the avgPx.
     */
    BigDecimal getAvgPx();

    /**
     * Gets the leavesQty.
     *
     * @return the leavesQty.
     */
    Long getLeavesQty();

    /**
     * Gets the cumQty.
     *
     * @return the cumQty.
     */
    Long getCumQty();
}
