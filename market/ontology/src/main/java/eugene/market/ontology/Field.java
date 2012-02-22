/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import jade.content.Concept;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for {@link Message}s fields.
 *
 * The following requirements should be met by extending classes:
 * <ul>
 * <li>All extending classes should be annotated with {@link Element} with name corresponding to their FIX tag.</li>
 * <li>Extending classes should be marked as final, to disable future extensions.</li>
 * <li>All extending classes should implement {@link Field#getTag()} and return their FIX tag as an {@link Integer};
 * usage of primitive type <code>int</code> is discouraged to avoid autoboxing.</li>
 * <li>All extending classes should implement {@link Field#isEnumField()} to indicate whether there is only a finite
 * set of valid values.</li>
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

    public static final String EMPTY_TAG = "EMPTY";

    public static final String TAG_FIELD_NAME = "TAG";

    private V value;

    /**
     * Empty contructor.
     */
    public Field() {
        this.value = null;
    }

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
    public abstract Integer getTag();

    /**
     * Checks if this {@link Field} is an enums.
     *
     * @return <code>true</code> if this {@link Field} is an enums, false otherwise.
     */
    @SuppressSlot
    public abstract Boolean isEnumField();

    /**
     * Gets valid values for this {@link Field} if it is an enums.
     *
     * @return valued values for this {@link Field}.
     */
    @SuppressSlot
    public Set<String> getEnumSet() throws IllegalAccessException {
        final Class<? extends Field> clazz = getClass();
        final Set<String> values = new HashSet<String>();
        for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
            if (!f.getName().equals(TAG_FIELD_NAME) && f.getType() == String.class) {
                values.add((String) f.get(this));
            }
        }
        return values;
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
        return (value != null ? value.equals(field.value) : field.value == null);
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
        b.append(getClass().getSimpleName());
        b.append("=").append(value);
        return b.toString();
    }
}
