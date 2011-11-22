package eugene.market.esma.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.ExecType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum ExecType {

    NEW("0"), PARTIAL_FILL("1"), FILL("2"), CANCELED("4"), REJECTED("8"), TRADE("F");

    private final String value;

    ExecType(final String value) {
        this.value = value;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.ExecType} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.ExecType} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.ExecType getExecType() {
        return new eugene.market.ontology.field.ExecType(value);
    }
}
