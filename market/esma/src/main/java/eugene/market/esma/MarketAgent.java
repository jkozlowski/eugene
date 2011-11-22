package eugene.market.esma;

import eugene.market.esma.impl.MarketServerImpl;
import eugene.market.esma.impl.MatchingEngineImpl;
import eugene.market.esma.impl.OrderBookImpl;
import eugene.market.ontology.MarketOntology;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.OntologyServer;
import jade.domain.FIPANames.ContentLanguage;
import jade.lang.acl.ACLMessage;

/**
 * Implements the Agent that acts as a Stock Market Exchange Order Matching Engine.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MarketAgent extends Agent {

    @Override
    public void setup() {
        getContentManager().registerLanguage(new SLCodec(), ContentLanguage.FIPA_SL);
        getContentManager().registerOntology(MarketOntology.getInstance());
        addBehaviour(new OntologyServer(this, MarketOntology
                .getInstance(), ACLMessage.REQUEST, new MarketServerImpl(this, new OrderBookImpl(new MatchingEngineImpl()))
        ));
    }
}
