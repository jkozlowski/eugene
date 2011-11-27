package eugene.market.esma.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum OrdStatus {

    NEW(eugene.market.ontology.field.OrdStatus.NEW),
    PARTIALLY_FILLED(eugene.market.ontology.field.OrdStatus.PARTIALLY_FILLED),
    FILLED(eugene.market.ontology.field.OrdStatus.FILLED),
    CANCELED(eugene.market.ontology.field.OrdStatus.CANCELLED),
    REJECTED(eugene.market.ontology.field.OrdStatus.REJECTED);

    private final String value;

    OrdStatus(final String value) {
        this.value = value;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.OrdStatus} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.OrdStatus} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.OrdStatus field() {
        return new eugene.market.ontology.field.OrdStatus(value);
    }
}
