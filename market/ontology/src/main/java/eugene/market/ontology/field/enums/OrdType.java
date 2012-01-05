package eugene.market.ontology.field.enums;

import eugene.market.ontology.message.NewOrderSingle;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum OrdType {

    MARKET(eugene.market.ontology.field.OrdType.MARKET),
    LIMIT(eugene.market.ontology.field.OrdType.LIMIT);

    public final String value;

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
     * Gets {@link OrdType} for this <code>newOrderSingle</code>.
     *
     * @param newOrderSingle order to get {@link OrdType} for.
     *
     * @return {@link OrdType} for this <code>newOrderSingle</code>
     */
    public static OrdType getOrdType(final NewOrderSingle newOrderSingle) {
        return MARKET.value.equals(newOrderSingle.getOrdType().getValue()) ? MARKET : LIMIT;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.OrdType field() {
        return new eugene.market.ontology.field.OrdType(value);
    }
}
