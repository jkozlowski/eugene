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
 * Calculated average price of all fills on this order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = AvgPx.TAG)
public final class AvgPx extends Field<Double> {

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
    public AvgPx(Double value) {
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
