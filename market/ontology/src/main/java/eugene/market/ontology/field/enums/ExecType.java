/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field.enums;

/**
 * Enum that mirrors {@link eugene.market.ontology.field.ExecType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public enum ExecType {

    NEW(eugene.market.ontology.field.ExecType.NEW),
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
