package com.ubs.eugene.demo.jade.messagepassing.internal;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Sends a specified message to a specified agent and finishes.
 *
 * @author Jakub D Kozlowski
 */
public class MessageSendingBehaviour extends OneShotBehaviour {

    /**
     * Message to send.
     *
     */
    private final String msg;

    /**
     * AID of Agent to send the message to.
     *
     */
    private final String to;

    /**
     * This constructor sets the owner agent for this
     * <code>MessageSendingBehaviour</code>, along with the message
     * to send and AID of Agent to send the message to.
     *
     * @param a The agent this behaviour must belong to.
     * @param msg The message to send.
     * @param to AID of Agent to send the message to.
     */
    public MessageSendingBehaviour(final Agent a, final String msg, final String to) {
        super(a);
        assert null != msg;
        assert null != to;
        this.msg = msg;
        this.to = to;
    }

    @Override
    public void action() {
        final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
        aclMessage.setSender(myAgent.getAID());
        aclMessage.setContent(msg);
        aclMessage.addReceiver(new AID(to, AID.ISLOCALNAME));
        myAgent.send(aclMessage);
    }
}
