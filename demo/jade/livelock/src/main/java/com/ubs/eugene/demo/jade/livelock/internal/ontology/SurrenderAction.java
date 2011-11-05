package com.ubs.eugene.demo.jade.livelock.internal.ontology;

import jade.content.AgentAction;

/**
 * Represents an action of surrendering a {@link Resource} in {@link ResourceOntology}.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class SurrenderAction implements AgentAction {

    private Resource resource;

    /**
     * Gets the resource for this <code>SurrenderAction</code>.
     *
     * @return the resource.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Sets the resource for this <code>SurrenderAction</code>.
     *
     * @param resource new resource.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
