/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import jade.content.AgentAction;
import jade.content.onto.annotations.SuppressSlot;

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
     * Sets the {@link Field} with this <code>tag</code>.
     *
     * @param tag   tag of {@link Field} to set.
     * @param field value to set.
     */
    public void setField(Integer tag, Field<?> field) {
        fields.put(tag, field);
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

        return fields.equals(((Message) o).fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public String toString() {
        final ToStringHelper toString = Objects.toStringHelper(getClass());

        for (final Field<?> field : fields.values()) {
            toString.add(field.getClass().getSimpleName(), field.getValue());
        }

        return toString.toString();
    }
}

