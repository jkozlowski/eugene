/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.demo.jade.livelock;

import eugene.demo.jade.livelock.internal.ActionInitiator;
import eugene.demo.jade.livelock.internal.ActionInitiator.RequestStatus;
import eugene.demo.jade.livelock.internal.ontology.RequestAction;
import eugene.demo.jade.livelock.internal.ontology.Resource;
import eugene.demo.jade.livelock.internal.ontology.ResourceOntology;
import eugene.demo.jade.livelock.internal.ontology.SurrenderAction;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

/**
 * Implements an agent that requests two resources in a specific order and finishes.
 *
 * This agent implements the following policy for acquiring resources:
 * <ul>
 * <li>Resources can only be acquired in a specific order.</li>
 * <li>If, after acquiring the first resource, the request to acquire the second resource fails, the agent surrenders
 * the first resource and retries the sequence after a delay.</li>
 * </ul>
 *
 * This agent expects two arguments: names of resources to acquire. The order of arguments indicates the resource
 * request order.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class WorkerAgent extends Agent {

    /**
     * AID of {@link ResourceServer}.
     */
    private static final AID RESOURCE_AGENT = new AID(ResourceServer.ID, AID.ISLOCALNAME);

    private static final String REGISTER_FIRST_RESOURCE = "register-first-resource";

    private static final String REGISTER_SECOND_RESOURCE = "register-second-resource";

    private static final String SURRENDER_FIRST_RESOURCE = "surrender-first-resource";

    private static final String FAIL = "fail";

    private static final String PRINTER = "print";

    private static final String SLEEPER = "sleeper";

    private static final int DEFAULT_WAIT = 3000;

    @Override
    protected void setup() {
        assert getArguments().length == 2;
        assert getArguments()[0] instanceof String;
        assert getArguments()[1] instanceof String;

        final String firstResource = (String) getArguments()[0];
        final String secondResource = (String) getArguments()[1];

        /**
         * Register {@link ResourceOntology}.
         *
         */
        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL);
        getContentManager().registerOntology(ResourceOntology.getInstance());

        /**
         * Construct the Finite State Machine.
         *
         */
        final FSMBehaviour fsm = new FSMBehaviour(this);

        /**
         * Register states.
         *
         */
        fsm.registerFirstState(new ActionInitiator(this, getRequestMessage(firstResource), firstResource),
                               REGISTER_FIRST_RESOURCE);
        fsm.registerState(new ActionInitiator(this, getRequestMessage(secondResource), secondResource),
                          REGISTER_SECOND_RESOURCE);
        fsm.registerState(new ActionInitiator(this, getSurrenderMessage(firstResource), firstResource),
                          SURRENDER_FIRST_RESOURCE);
        fsm.registerState(new OneShotBehaviour() {

            @Override
            public void action() {
                System.out.println(getAID() + ": Sleeping for " + DEFAULT_WAIT + " milliseconds");
                doWait(DEFAULT_WAIT);
            }
        }, SLEEPER);
        fsm.registerLastState(new OneShotBehaviour() {

            @Override
            public void action() {
                System.out.println(getAID() + ": Failed to request it's first resource!!!!");
            }
        }, FAIL);
        fsm.registerLastState(new OneShotBehaviour() {

            @Override
            public void action() {
                System.out.println(getAID() + ": Succeded in requesting both resources!!!!");
            }
        }, PRINTER);

        /**
         * Register complete transition.
         *
         */
        fsm.registerDefaultTransition(REGISTER_FIRST_RESOURCE, SLEEPER);
        fsm.registerDefaultTransition(SLEEPER, REGISTER_SECOND_RESOURCE);
        fsm.registerDefaultTransition(REGISTER_SECOND_RESOURCE, PRINTER);
        fsm.registerDefaultTransition(SURRENDER_FIRST_RESOURCE, REGISTER_FIRST_RESOURCE);

        /**
         * Register alternative transitions.
         *
         */
        fsm.registerTransition(REGISTER_FIRST_RESOURCE, FAIL, RequestStatus.FAILURE.status);
        fsm.registerTransition(REGISTER_SECOND_RESOURCE, SURRENDER_FIRST_RESOURCE, RequestStatus.FAILURE.status);

        addBehaviour(fsm);
    }

    private ACLMessage getRequestMessage(final String resourceName) {

        final ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

        request.addReceiver(RESOURCE_AGENT);
        request.setOntology(ResourceOntology.getInstance().getName());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);

        try {
            final Resource resource = new Resource();
            resource.setName(resourceName);
            final RequestAction requestAction = new RequestAction();
            requestAction.setResource(resource);

            Action actExpr = new Action(RESOURCE_AGENT, requestAction);
            getContentManager().fillContent(request, actExpr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }

    private ACLMessage getSurrenderMessage(final String resourceName) {
        final ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

        request.addReceiver(RESOURCE_AGENT);
        request.setOntology(ResourceOntology.getInstance().getName());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);

        try {
            final Resource resource = new Resource();
            resource.setName(resourceName);
            final SurrenderAction surrenderAction = new SurrenderAction();
            surrenderAction.setResource(resource);

            Action actExpr = new Action(RESOURCE_AGENT, surrenderAction);
            getContentManager().fillContent(request, actExpr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }
}
