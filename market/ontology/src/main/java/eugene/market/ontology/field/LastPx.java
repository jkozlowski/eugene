/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Price of execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = LastPx.TAG)
public final class LastPx extends Field<Double> {

    public static final String TAG = "31";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public LastPx() {
    }

    /**
     * {@inheritDoc}
     */
    public LastPx(Double value) {
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
}
