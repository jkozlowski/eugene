/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import eugene.market.ontology.message.ExecutionReport;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Describes the specific {@link ExecutionReport} status.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = ExecType.TAG)
public final class ExecType extends Field<String> {

    public static final String TAG = "150";

    public static final Integer TAGi = Integer.parseInt(TAG);

    public static final String NEW = "0";

    public static final String CANCELED = "4";

    public static final String REJECTED = "8";

    public static final String TRADE = "F";

    /**
     * {@inheritDoc}
     */
    public ExecType() {
    }

    /**
     * {@inheritDoc}
     */
    public ExecType(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = {NEW, CANCELED, REJECTED, TRADE})
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
