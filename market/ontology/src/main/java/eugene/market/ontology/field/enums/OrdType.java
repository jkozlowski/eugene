/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field.enums;

import eugene.market.ontology.message.NewOrderSingle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum OrdType {

    MARKET(eugene.market.ontology.field.OrdType.MARKET),
    LIMIT(eugene.market.ontology.field.OrdType.LIMIT);

    private final String value;

    OrdType(final String value) {
        this.value = value;
    }

    /**
     * Checks if this {@link OrdType} is {@link OrdType#MARKET}.
     *
     * @return <code>true</code> if this {@link OrdType} is {@link OrdType#MARKET}, <code>false</code> otherwise.
     */
    public boolean isMarket() {
        return this == MARKET;
    }

    /**
     * Checks if this {@link OrdType} is {@link OrdType#LIMIT}.
     *
     * @return <code>true</code> if this {@link OrdType} is {@link OrdType#LIMIT}, <code>false</code> otherwise.
     */
    public boolean isLimit() {
        return this == LIMIT;
    }

    /**
     * Gets the {@link #LIMIT} value.
     * 
     * @return the {@link #LIMIT} value.
     */
    public static OrdType limit() {
        return LIMIT;
    }

    /**
     * Gets the {@link #MARKET} value.
     *
     * @return the {@link #MARKET} value.
     */
    public static OrdType market() {
        return MARKET;
    }

    /**
     * Gets {@link OrdType} for this <code>newOrderSingle</code>.
     *
     * @param newOrderSingle order to get {@link OrdType} for.
     *
     * @return {@link OrdType} for this <code>newOrderSingle</code>
     *
     * @throws NullPointerException     if <code>newOrderSingle</code> or {@link NewOrderSingle#getOrdType()} are null.
     * @throws IllegalArgumentException if value returned from {@link NewOrderSingle#getOrdType()} is invalid.
     */
    public static OrdType getOrdType(final NewOrderSingle newOrderSingle)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(newOrderSingle);
        checkNotNull(newOrderSingle.getOrdType());

        if (MARKET.value.equals(newOrderSingle.getOrdType().getValue())) {
            return MARKET;
        }
        else if (LIMIT.value.equals(newOrderSingle.getOrdType().getValue())) {
            return LIMIT;
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gets the {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.OrdType field() {
        return new eugene.market.ontology.field.OrdType(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this == MARKET ? "MARKET" : "LIMIT";
    }
}
