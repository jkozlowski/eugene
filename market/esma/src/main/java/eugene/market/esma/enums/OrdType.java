package eugene.market.esma.enums;

import eugene.market.ontology.message.NewOrderSingle;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum OrdType {

    MARKET("1"), LIMIT("2");

    public final String value;

    OrdType(final String value) {
        this.value = value;
    }

    /**
     * Gets {@link OrdType} for this <code>newOrderSingle</code>.
     *
     * @param newOrderSingle order to get {@link OrdType} for.
     *
     * @return {@link OrdType} for this <code>newOrderSingle</code>
     */
    public static OrdType getOrdType(final NewOrderSingle newOrderSingle) {
        if (MARKET.value.equals(newOrderSingle.getOrdType().getValue())) {
            return MARKET;
        }
        else {
            return LIMIT;
        }
    }

    /**
     * Gets the {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.OrdType} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.OrdType getOrdType() {
        return new eugene.market.ontology.field.OrdType(value);
    }
}
