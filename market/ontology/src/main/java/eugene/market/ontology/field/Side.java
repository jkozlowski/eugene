package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

import javax.swing.text.html.HTML.Tag;

/**
 * Side of order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = Side.TAG)
public final class Side extends Field<String> {

    public static final String TAG = "54";

    public static final int TAGi = 54;

    public static final String BUY = "1";

    public static final String SELL = "1";

    public Side(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = { BUY, SELL })
    public String getValue() {
        return super.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTag() {
        return TAGi;
    }
}
