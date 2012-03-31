/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

import java.math.BigDecimal;

/**
 * Average price of all fills of an order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = AvgPx.TAG)
public final class AvgPx extends Field<BigDecimal> {

    public static final String TAG = "6";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public AvgPx() {
    }

    /**
     * {@inheritDoc}
     */
    public AvgPx(BigDecimal value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressSlot
    public Integer getTag() {
        return TAGi;
    }

    @Override
    @SuppressSlot
    public Boolean isEnumField() {
        return Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AvgPx field = (AvgPx) o;
        return (getValue() != null ? getValue().compareTo(field.getValue()) == 0 : field.getValue() == null);
    }
}
