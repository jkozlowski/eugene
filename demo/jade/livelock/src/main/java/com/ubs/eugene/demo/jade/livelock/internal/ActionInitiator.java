package com.ubs.eugene.demo.jade.livelock.internal;

import com.ubs.eugene.demo.jade.livelock.internal.ontology.RequestAction;
import com.ubs.eugene.demo.jade.livelock.internal.ontology.SurrenderAction;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import static com.ubs.eugene.demo.jade.livelock.internal.ActionInitiator.RequestStatus.FAILURE;
import static com.ubs.eugene.demo.jade.livelock.internal.ActionInitiator.RequestStatus.SUCCESS;

/**
 * Issues {@link RequestAction}s and {@link SurrenderAction}s and exits with {@link RequestStatus#SUCCESS}
 * upon success and {@link RequestStatus#FAILURE} otherwise.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class ActionInitiator extends SimpleAchieveREInitiator {

    /**
     * Status of the request. {@link RequestStatus#FAILURE} by default.
     */
    private int requestStatus = FAILURE.status;

    public enum RequestStatus {

        SUCCESS(0), FAILURE(1);

        public final int status;

        RequestStatus(final int status) {
            this.status = status;
        }
    }

    /**
     * Resource to send this action for.
     *
     */
    private final String resource;

    /**
     * Constructs an instance of <code>RequestResourceResponder</code> that will send this <code>msg</code>.
     *
     * @param a   is the reference to the Agent object.
     * @param msg the <code>ACLMessage</code> to send.
     */
    public ActionInitiator(Agent a, ACLMessage msg, String resource) {
        super(a, msg);
        this.resource = resource;
    }

    @Override
    protected void handleInform(ACLMessage msg) {
        System.out.println(myAgent.getAID() + ": SUCCESS: " + resource);
        this.requestStatus = SUCCESS.status;
    }

    @Override
    public int onEnd() {
        if (FAILURE.status == requestStatus) {
            System.out.println(myAgent.getAID() + ": FAILURE: " + resource);
        }
        return requestStatus;
    }
}
