package eugene.market.ontology.field.enums;

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

    /**
     * Checks if this {@link OrdStatus} is {@link OrdStatus#NEW}.
     *
     * @return <code>true</code> if this {@link OrdStatus} is {@link OrdStatus#NEW}, <code>false</code> otherwise.
     */
    public boolean isNew() {
        return NEW.equals(this);
    }

    /**
     * Checks if this {@link OrdStatus} is {@link OrdStatus#PARTIALLY_FILLED}.
     *
     * @return <code>true</code> if this {@link OrdStatus} is {@link OrdStatus#PARTIALLY_FILLED}, <code>false</code>
     *         otherwise.
     */
    public boolean isPartiallyFilled() {
        return PARTIALLY_FILLED.equals(this);
    }

    /**
     * Checks if this {@link OrdStatus} is {@link OrdStatus#FILLED}.
     *
     * @return <code>true</code> if this {@link OrdStatus} is {@link OrdStatus#FILLED}, <code>false</code> otherwise.
     */
    public boolean isFilled() {
        return FILLED.equals(this);
    }

    /**
     * Checks if this {@link OrdStatus} is {@link OrdStatus#CANCELED}.
     *
     * @return <code>true</code> if this {@link OrdStatus} is {@link OrdStatus#CANCELED}, <code>false</code> otherwise.
     */
    public boolean isCanceled() {
        return CANCELED.equals(this);
    }

    /**
     * Checks if this {@link OrdStatus} is {@link OrdStatus#REJECTED}.
     *
     * @return <code>true</code> if this {@link OrdStatus} is {@link OrdStatus#REJECTED}, <code>false</code> otherwise.
     */
    public boolean isRejected() {
        return REJECTED.equals(this);
    }
}
