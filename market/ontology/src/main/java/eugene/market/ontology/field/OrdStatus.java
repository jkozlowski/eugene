/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Identifies current status of order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrdStatus.TAG)
public final class OrdStatus extends Field<String> {

    public static final String TAG = "39";

    public static final Integer TAGi = Integer.parseInt(TAG);

    public static final String NEW = "0";

    public static final String PARTIALLY_FILLED = "1";

    public static final String FILLED = "2";

    public static final String CANCELLED = "4";

    public static final String REJECTED = "8";

    /**
     * {@inheritDoc}
     */
    public OrdStatus() {
    }

    /**
     * {@inheritDoc}
     */
    public OrdStatus(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = {NEW, PARTIALLY_FILLED, FILLED, CANCELLED, REJECTED})
    public String getValue() {
        return super.getValue();
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
        return Boolean.TRUE;
    }
}
