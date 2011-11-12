package eugene.market.ontology.field;

import eugene.market.ontology.message.Message;
import jade.content.Concept;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Base class for {@link Message}s fields.
 *
 * The following requirements should be met by extending classes:
 * <ul>
 *     <li>All extending classes should be annotated with {@link Element} with name corresponding to their FIX tag.</li>
 *     <li>Extending classes should be marked as final, to disable future extensions.</li>
 * </ul>
 *
 *
 * When a field has a finite set of valid values, the implementing class should:
 * <ul>
 * <li>Override {@link Field#getValue()} and place {@link Slot} annotation with {@link Slot#mandatory()} equal to
 * <code>true</code> and {@link Slot#permittedValues()} equal to the finite set of valid values.</li>
 * <li>For each of the valid values provide a <code>public static final</code> constant for ease of use.</li>
 * </ul>
 *
 * @author Jakub D Kozlowski
 * @see Element
 * @see Slot
 * @since 0.2
 */
public abstract class Field<V> implements Concept {

    private static final String EMPTY_TAG = "EMPTY";

    private V value;

    /**
     * Default constructor.
     *
     * @param value the field's value.
     */
    public Field(V value) {
        this.value = value;
    }

    /**
     * Gets the field's tag.
     *
     * @return the tag.
     */
    @SuppressSlot
    public String getTag() {
        return null != getClass().getAnnotation(Element.class) ? EMPTY_TAG
                                                               : getClass().getAnnotation(Element.class).name();
    }

    /**
     * Get the field's value.
     *
     * @return the field's value.
     */
    @Slot(mandatory = true)
    public V getValue() {
        return value;
    }

    /**
     * Sets the field's value.
     *
     * @param value the field's value.
     */
    public void setValue(final V value) {
        this.value = value;
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

        final Field field = (Field) o;
        if (value != null ? !value.equals(field.value) : field.value != null) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        return b.append(getTag()).append(':').append(getTag()).append("=").append(value).toString();
    }
}
