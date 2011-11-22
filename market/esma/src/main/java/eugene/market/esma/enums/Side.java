package eugene.market.esma.enums;

import eugene.market.ontology.message.NewOrderSingle;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.Side}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum Side {

    BUY("1"), SELL("2");

    public final String value;

    Side(final String value) {
        this.value = value;
    }

    /**
     * Gets {@link Side} for this <code>newOrderSingle</code>.
     *
     * @param newOrderSingle order to get {@link Side} for.
     *
     * @return {@link Side} for this <code>newOrderSingle</code>
     */
    public static Side getSide(final NewOrderSingle newOrderSingle) {
        if (BUY.value.equals(newOrderSingle.getSide().getValue())) {
            return BUY;
        }
        else {
            return SELL;
        }
    }

    /**
     * Gets the {@link eugene.market.ontology.field.Side} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.Side} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.Side getSide() {
        return new eugene.market.ontology.field.Side(value);
    }
}
