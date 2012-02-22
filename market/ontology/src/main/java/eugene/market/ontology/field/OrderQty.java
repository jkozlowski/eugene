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
 * Quantity ordered.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrderQty.TAG)
public final class OrderQty extends Field<Long> {

    public static final String TAG = "38";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public OrderQty() {
    }

    /**
     * {@inheritDoc}
     */
    public OrderQty(Long value) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressSlot
    public Boolean isEnumField() {
        return Boolean.FALSE;
    }
}
