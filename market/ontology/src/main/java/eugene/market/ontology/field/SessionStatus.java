package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import eugene.market.ontology.message.Logon;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Session status at time of {@link Logon}. Field is intended to be used when the {@link Logon} is sent as an
 * acknowledgement from acceptor of the {@link Logon} message.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = SessionStatus.TAG)
public class SessionStatus extends Field<String> {

    public static final String TAG = "1409";

    public static final Integer TAGi = Integer.parseInt(TAG);
    
    public static final String SESSION_ACTIVE = "1";
    
    /**
     * {@inheritDoc}
     */
    public SessionStatus() {
    }

    /**
     * {@inheritDoc}
     */
    public SessionStatus(String value) {
        super(value);
    }

    @Override
    @Slot(mandatory = true, permittedValues = { SESSION_ACTIVE })
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
