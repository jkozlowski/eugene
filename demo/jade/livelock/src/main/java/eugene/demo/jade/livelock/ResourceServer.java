/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.demo.jade.livelock;

import eugene.demo.jade.livelock.internal.ontology.RequestAction;
import eugene.demo.jade.livelock.internal.ontology.Resource;
import eugene.demo.jade.livelock.internal.ontology.ResourceOntology;
import eugene.demo.jade.livelock.internal.ontology.SurrenderAction;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OntologyServer;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

/**
 * Grants resources to agents.
 *
 * This agent implements the following policy:
 * <ul>
 * <li>Resources are granted on a first-come, first-served basis.</li>
 * <li>Resources are granted exclusively - no resource sharing.</li>
 * <li>If a resource is granted, it remains so until it is surrendered.</li>
 * <li>If a request is made for a busy resource, the request is rejected; otherwise it succeeds.</li>
 * </ul>
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class ResourceServer extends Agent {

    /**
     * ID of this server.
     */
    public static final String ID = "resource-service";

    /**
     * <code>DataStore</code> to use.
     */
    private final DataStore ds = new DataStore();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL);
        getContentManager().registerOntology(ResourceOntology.getInstance());
        addBehaviour(new OntologyServer(this, ResourceOntology.getInstance(), ACLMessage.REQUEST, this));
    }

    public void serveSurrenderActionRequest(SurrenderAction surrenderAction, ACLMessage msg) {
        final Resource resource = surrenderAction.getResource();
        ds.put(resource, Boolean.FALSE);

        final ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(msg.getContent());
        send(reply);
    }

    public void serveRequestActionRequest(RequestAction requestAction, ACLMessage msg) {

        final ACLMessage reply = msg.createReply();

        /**
         * Lookup the status of the resource.
         *
         */
        final Resource resource = requestAction.getResource();
        final Boolean status =
                ds.containsKey(resource) ? (Boolean) ds.get(resource)
                                         : Boolean.FALSE;

        /**
         * Resource already requested.
         *
         */
        if (status) {
            reply.setPerformative(ACLMessage.REFUSE);
            send(reply);
        }

        /**
         * Grant the resource.
         *
         */
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(msg.getContent());
        ds.put(resource, Boolean.TRUE);
        send(reply);
    }
}
