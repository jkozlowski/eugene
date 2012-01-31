package eugene.simulation.agent.impl;

import eugene.market.esma.MarketAgent;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.utils.BehaviourResult;
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

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts the {@link MarketAgent}, sends a {@link Start} message to it and waits for a {@link Started} reply from it.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class InitializeMarketAgentBehaviour extends SequentialBehaviour {

    private final Logger LOG = Logger.getLogger(InitializeMarketAgentBehaviour.class.getName());

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
                        LOG.severe(e.toString());
                    }
                    catch (OntologyException e) {
                        LOG.severe(e.toString());
                    }
                }
            };

            addSubBehaviour(sender);
        }
        catch (StaleProxyException e) {
            LOG.severe(e.toString());
        }
        catch (CodecException e) {
            LOG.severe(e.toString());
        }
        catch (OntologyException e) {
            LOG.severe(e.toString());
        }
        catch (ControllerException e) {
            LOG.severe(e.toString());
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
