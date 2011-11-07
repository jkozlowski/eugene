package eugene.demo.jade.livelock.internal.ontology;

import jade.content.AgentAction;

/**
 * Represents an action of requesting a {@link Resource} in {@link ResourceOntology}.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class RequestAction implements AgentAction {

    private Resource resource;

    /**
     * Gets the resource for this <code>RequestAction</code>.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Sets the resource for this <code>RequestAction</code>.
     *
     * @param resource new resource.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
