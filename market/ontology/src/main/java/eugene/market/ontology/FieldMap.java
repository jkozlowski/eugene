package eugene.market.ontology;

import jade.content.Concept;
import jade.content.onto.annotations.SuppressSlot;

import java.util.Iterator;
import java.util.TreeMap;


/**
 * Field container used by messages and composites.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public abstract class FieldMap implements Concept {

    private final TreeMap<Integer, Field<?>> fields = new TreeMap<Integer, Field<?>>();

    /**
     * Returns true if {@link Field} with this <code>tag</code> exists in this {@link FieldMap}.
     *
     * @param tag tag to check.
     *
     * @return true if {@link Field} with this tag exists in this {@link FieldMap}, false otherwise.
     */
    public boolean isSetField(Integer tag) {
        return fields.containsKey(tag);
    }

    /**
     * Returns true if this <code>field</code> exists in this {@link FieldMap}.
     *
     * @param field field to check.
     *
     * @return true if this <code>field</code> exists in this {@link FieldMap},
     *         false otherwise.
     */
    public boolean isSetField(Field<?> field) {
        return isSetField(field.getTag());
    }

    /**
     * Gets a {@link Field} with this <code>tag</code> if it exists in this {@link FieldMap},
     * otherwise returns <code>defaultValue</code>
     *
     * @param tag          tag to return.
     * @param defaultValue default value to return if a {@link Field} with this <code>tag</code> does not exist in this
     *                     {@link FieldMap}.
     *
     * @return {@link Field} with this <code>tag</code> if it exists in this {@link FieldMap},
     * <code>defaultValue</code>
     *         otherwise.
     */
    @SuppressSlot
    public Field<?> getField(Integer tag, Field<?> defaultValue) {
        final Field<?> f = fields.get(tag);
        if (f == null) {
            return defaultValue;
        }
        return f;
    }

    /**
     * Sets the {@link Field} with this <code>tag</code>.
     *
     * @param tag tag of {@link Field} to set.
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

