package eugene.market.ontology.field;

import eugene.market.ontology.message.Message;
import jade.content.Concept;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Base class for {@link Message}s fields.
 *
 * All extending classes should be annotated with {@link Element} with name corresponding to their FIX tag.
 *
 * @author Jakub D Kozlowski
 * @see Element
 * @see Slot
 * @since 0.2
 */
public abstract class Field<T> implements Concept {

    private static final String EMPTY_TAG = "EMPTY";

    private T object;

    /**
     * Default constructor.
     *
     * @param object an object representing the field's value.
     */
    public Field(T object) {
        this.object = object;
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
     * @return an object representing the field's value.
     */
    @Slot(mandatory = true)
    public T getObject() {
        return object;
    }

    /**
     * Sets the field's value.
     *
     * @param object an object representing the field's value.
     */
    public void setObject(final T object) {
        this.object = object;
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
        if (object != null ? !object.equals(field.object) : field.object != null) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        return b.append(getTag()).append(':').append(getTag()).append("=").append(object).toString();
    }
}
