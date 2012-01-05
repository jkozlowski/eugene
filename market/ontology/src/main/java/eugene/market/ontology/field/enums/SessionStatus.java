package eugene.market.ontology.field.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.SessionStatus}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public enum SessionStatus {

    SESSION_ACTIVE(eugene.market.ontology.field.SessionStatus.SESSION_ACTIVE);

    private final String value;

    SessionStatus(final String value) {
        this.value = value;
    }

    /**
     * Gets the {@link eugene.market.ontology.field.SessionStatus} for this <code>enum</code>.
     *
     * @return {@link eugene.market.ontology.field.SessionStatus} for this <code>enum</code>.
     */
    public eugene.market.ontology.field.SessionStatus field() {
        return new eugene.market.ontology.field.SessionStatus(value);
    }
}
