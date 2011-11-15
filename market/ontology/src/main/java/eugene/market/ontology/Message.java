package eugene.market.ontology;

import jade.content.AgentAction;
import jade.content.onto.annotations.SuppressSlot;

import java.util.Iterator;
import java.util.TreeMap;


/**
 * Interface implemented by messages that can be sent between Agents and the Market.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public abstract class Message implements AgentAction {

    private final TreeMap<Integer, Field<?>> fields = new TreeMap<Integer, Field<?>>();

    /**
     * Returns true if {@link Field} with this <code>tag</code> exists in this {@link Message}.
     *
     * @param tag tag to check.
     *
     * @return true if {@link Field} with this tag exists in this {@link Message}, false otherwise.
     */
    public boolean isSetField(Integer tag) {
        return fields.containsKey(tag);
    }

    /**
     * Returns true if this <code>field</code> exists in this {@link Message}.
     *
     * @param field field to check.
     *
     * @return true if this <code>field</code> exists in this {@link Message},
     *         false otherwise.
     */
    public boolean isSetField(Field<?> field) {
        return isSetField(field.getTag());
    }

    /**
     * Gets a {@link Field} with this <code>tag</code> if it exists in this {@link Message},
     * otherwise returns <code>null</code>.
     *
     * @param tag tag to return.
     * @param <F> type of field to cast to.
     *
     * @return {@link Field} with this <code>tag</code> if it exists in this {@link Message}, <code>null</code>
     *         otherwise.
     *
     * @throws ClassCastException if <code>F</code> is not the type of the returned field.
     */
    @SuppressSlot
    public <F extends Field<?>> F getField(Integer tag) {
        return (F) fields.get(tag);
    }

    /**
     * Sets the {@link Field} with this <code>tag</code>.
     *
     * @param tag   tag of {@link Field} to set.
     * @param field value to set.
     */
    public void setField(Integer tag, Field<?> field) {
        fields.put(tag, field);
    }

    /**
     * Removes the {@link Field} with this <code>tag</code>.
     *
     * @param tag tag of {@link Field} to remove.
     */
    public void removeField(Integer tag) {
        fields.remove(tag);
    }

    /**
     * Gets the message's type.
     *
     * @return the tag.
     */
    @SuppressSlot
    public abstract String getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        if (fields != null ? !fields.equals(message.fields) : message.fields != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return fields != null ? fields.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        b.append(getClass().getName()).append("[");

        final Iterator<Field<?>> iterator = fields.values().iterator();
        while (iterator.hasNext()) {
            final Field<?> f = iterator.next();
            b.append(f.getClass()).append("=").append(f.getValue());

            if (iterator.hasNext()) {
                b.append(", ");
            }
        }

        b.append("]");
        return toString();
    }
}

