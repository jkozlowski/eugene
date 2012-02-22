/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.esma.MarketAgent;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts the {@link MarketAgent}, sends a {@link Start} message to it and waits for a {@link Started} reply from it.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class InitializeMarketAgentBehaviour extends SequentialBehaviour {
    
    private static final String ERROR_MSG = "Could not start the Market Agent";

    private final Logger LOG = LoggerFactory.getLogger(InitializeMarketAgentBehaviour.class);

    private final BehaviourResult<AID> result;

    private final MarketAgent marketAgent;

    /**
     * Creates a {@link InitializeMarketAgentBehaviour} that will start this <code>marketAgent</code>.
     *
     * @param marketAgent {@link MarketAgent} to start.
     */
    public InitializeMarketAgentBehaviour(final MarketAgent marketAgent) {
        checkNotNull(marketAgent);
        this.marketAgent = marketAgent;
        result = new BehaviourResult<AID>();
    }

    @Override
    public void onStart() {
        try {
            final AgentContainer container = myAgent.getContainerController();
            final String marketName = MarketAgent.AGENT_NAME + marketAgent + System.currentTimeMillis();
            final AgentController controller = container.acceptNewAgent(marketName, marketAgent);
            controller.start();

            final AID marketAID = new AID(marketName, AID.ISLOCALNAME);
            final Action action = new Action(new AID(marketName, AID.ISLOCALNAME), new Start());
            final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.addReceiver(marketAID);
            aclMessage.setLanguage(SimulationOntology.LANGUAGE);
            aclMessage.setOntology(SimulationOntology.NAME);
            myAgent.getContentManager().fillContent(aclMessage, action);

            final AchieveREInitiator sender = new AchieveREInitiator(myAgent, aclMessage) {

                @Override
                public void handleInform(final ACLMessage aclMessage) {

                    try {
                        final ContentElement ce = myAgent.getContentManager().extractContent(aclMessage);
                        if (ce instanceof Action &&
                                null != ((Action) ce).getAction() &&
                                ((Action) ce).getAction() instanceof Started) {

                            result.success(marketAID);
                        }
                    }
                    catch (CodecException e) {
                        LOG.error(ERROR_MSG, e);
                    }
                    catch (OntologyException e) {
                        LOG.error(ERROR_MSG, e);
                    }
                }
            };

            addSubBehaviour(sender);
        }
        catch (StaleProxyException e) {
            LOG.error(ERROR_MSG, e);
        }
        catch (CodecException e) {
            LOG.error(ERROR_MSG, e);
        }
        catch (OntologyException e) {
            LOG.error(ERROR_MSG, e);
        }
        catch (ControllerException e) {
            LOG.error(ERROR_MSG, e);
        }
    }

    /**
     * Gets the {@link BehaviourResult} used by this {@link InitializeMarketAgentBehaviour}.
     *
     * @return {@link BehaviourResult} used by this {@link InitializeMarketAgentBehaviour}.
     */
    public BehaviourResult<AID> getResult() {
        return result;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
