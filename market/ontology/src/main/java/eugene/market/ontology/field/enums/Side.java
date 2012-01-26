package eugene.market.ontology.field.enums;

import eugene.market.ontology.message.NewOrderSingle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.Side}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum Side {

    BUY(eugene.market.ontology.field.Side.BUY),
    SELL(eugene.market.ontology.field.Side.SELL);

    private final String value;

    Side(final String value) {
        this.value = value;
    }

    /**
     * Checks if this {@link Side} is {@link Side#BUY}.
     *
     * @return <code>true</code> if this {@link Side} is {@link Side#BUY}, <code>false</code> otherwise.
     */
    public boolean isBuy() {
        return this == BUY;
    }

    /**
     * Checks if this {@link Side} is {@link Side#SELL}.
     *
     * @return <code>true</code> if this {@link Side} is {@link Side#SELL}, <code>false</code> otherwise.
     */
    public boolean isSell() {
        return this == SELL;
    }

    /**
     * Gets the opposite {@link Side}.
     *
     * @return {@link Side#BUY} if called on {@link Side#SELL} and vice versa.
     */
    public Side getOpposite() {
        return isBuy() ? SELL : BUY;
    }

    /**
     * Gets {@link Side} for this <code>field</code>.
     *
     * @param field {@link eugene.market.ontology.field.Side} to parse.
     *
     * @return {@link Side} for this <code>field</code>.
     *
     * @throws NullPointerException     if <code>field</code> or {@link eugene.market.ontology.field.Side#getValue()}
     *                                  are null.
     * @throws IllegalArgumentException if value returned from {@link eugene.market.ontology.field.Side#getValue()}
     *                                  is invalid.
     */
    public static Side parse(final eugene.market.ontology.field.Side field)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(field);
        checkNotNull(field.getValue());

        if (BUY.value.equals(field.getValue())) {
            return BUY;
        }
        else if (SELL.value.equals(field.getValue())) {
            return SELL;
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gets {@link Side} for this <code>newOrderSingle</code>.
     *
     * @param newOrderSingle order to get {@link Side} for.
     *
     * @return {@link Side} for this <code>newOrderSingle</code>.
     *
     * @throws NullPointerException     if <code>field</code> or {@link NewOrderSingle#getSide()} are null.
     * @throws IllegalArgumentException if value returned from {@link NewOrderSingle#getSide()} is invalid.
     */
    public static Side getSide(final NewOrderSingle newOrderSingle)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(newOrderSingle);
        checkNotNull(newOrderSingle.getSide());

        if (BUY.value.equals(newOrderSingle.getSide().getValue())) {
            return BUY;
        } else if (SELL.value.equals(newOrderSingle.getSide().getValue())) {
            return SELL;
        }
        
        throw new IllegalArgumentException();
    }

    /**
     * Gets the {@link eugene.market.ontology.field.Side} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.Side} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.Side field() {
        return new eugene.market.ontology.field.Side(value);
    }
}
