package eugene.market.esma.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum OrdStatus {
    NEW("0"), PARTIALLY_FILLED("1"), FILLED("2"), CANCELLED("4"), REJECTED("8");

    private final String value;

    OrdStatus(final String value) {
        this.value = value;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.OrdStatus} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.OrdStatus} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.OrdStatus getOrdStatus() {
        return new eugene.market.ontology.field.OrdStatus(value);
    }
}
