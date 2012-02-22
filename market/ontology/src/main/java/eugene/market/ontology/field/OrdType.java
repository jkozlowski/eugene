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
 * Order type.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrdType.TAG)
public final class OrdType extends Field<String> {

    public static final String TAG = "40";

    public static final Integer TAGi = Integer.parseInt(TAG);

    public static final String MARKET = "1";

    public static final String LIMIT = "2";

    /**
     * {@inheritDoc}
     */
    public OrdType() {
    }

    /**
     * {@inheritDoc}
     */
    public OrdType(String value) {
        super(value);
    }

    @Override
    @Slot(mandatory = true, permittedValues = {LIMIT, MARKET})
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
