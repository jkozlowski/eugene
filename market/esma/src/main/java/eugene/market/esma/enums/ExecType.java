package eugene.market.esma.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.ExecType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum ExecType {

    NEW(eugene.market.ontology.field.ExecType.NEW),
    PARTIAL_FILL(eugene.market.ontology.field.ExecType.PARTIAL_FILL),
    FILL(eugene.market.ontology.field.ExecType.FILL),
    CANCELED(eugene.market.ontology.field.ExecType.CANCELED),
    REJECTED(eugene.market.ontology.field.ExecType.REJECTED),
    TRADE(eugene.market.ontology.field.ExecType.TRADE);

    private final String value;

    ExecType(final String value) {
        this.value = value;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.ExecType} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.ExecType} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.ExecType field() {
        return new eugene.market.ontology.field.ExecType(value);
    }
}
