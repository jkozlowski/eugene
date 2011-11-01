package com.ubs.eugene.demo.livelock.internal.ontology;

import jade.content.Concept;

/**
 * Represents a resource that can be requested or surrendered in {@link ResourceOntology}.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class Resource implements Concept {

    private String name;

    /**
     * Gets the name of this <code>Resource</code>.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this <code>Resource</code>.
     *
     * @param name new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Resource resource = (Resource) o;

        if (name != null ? !name.equalsIgnoreCase(resource.name) : resource.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
