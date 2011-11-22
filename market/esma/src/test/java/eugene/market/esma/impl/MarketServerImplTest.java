package eugene.market.esma.impl;

import eugene.market.esma.Order;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OntologyServer;
import jade.domain.FIPANames.ContentLanguage;
import jade.imtp.memory.MemoryProfile;
import jade.lang.acl.ACLMessage;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static jade.core.Runtime.instance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests {@link MarketServerImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MarketServerImplTest {

    public AgentContainer agentContainer;

    public static final String TRADER_AGENT = "trader";

    public static final String MARKET_AGENT = "market";

    public GatewayAgent traderAgent;

    public GatewayAgent marketAgent;

    public AgentController traderAgentController;

    public AgentController marketAgentController;

    public static final String LANGUAGE = ContentLanguage.FIPA_SL;

    public static final String ClOrdID = "11";

    public static final Long OrderQty = 2L;

    public static final Double Price = 1.2;

    public static final String Symbol = "VOD.L";

    /**
     * Creates the container.
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        agentContainer = instance().createMainContainer(profile);

        traderAgent = new GatewayAgent();
        traderAgentController = agentContainer.acceptNewAgent(TRADER_AGENT, traderAgent);
        initAgent(traderAgent);

        marketAgent = new GatewayAgent();
        marketAgentController = agentContainer.acceptNewAgent(MARKET_AGENT, marketAgent);
        initAgent(marketAgent);

        System.out.println("Setting up");
    }

    /**
     * Initializes {@link Agent}'s language and ontology.
     *
     * @param a agent to initialize.
     */
    public static void initAgent(final Agent a) {
        a.getContentManager().registerLanguage(new SLCodec(), LANGUAGE);
        a.getContentManager().registerOntology(MarketOntology.getInstance());
    }

    @Test
    public void testSendNewOrderSingleNoExecution() throws StaleProxyException, InterruptedException {
        final Set<NewOrderSingle> toSend = new HashSet<NewOrderSingle>();
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(ClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(OrderQty));
        newOrder.setPrice(new Price(Price));
        newOrder.setSymbol(new Symbol(Symbol));
        newOrder.setOrdType(new OrdType(OrdType.LIMIT));
        toSend.add(newOrder);

        final Behaviour marketServerBehaviour = new OntologyServer(marketAgent, MarketOntology.getInstance(),
                                                                   ACLMessage.REQUEST,
                                                                   new MarketServerImpl(marketAgent,
                                                                                        new OrderBookImpl(new MatchingEngineImpl())));
        final Event marketEvent = new Event(-1, marketServerBehaviour);
        marketAgentController.start();
        marketAgentController.putO2AObject(marketEvent, AgentController.ASYNC);

        final TraderBehaviour traderBehaviour = new TraderBehaviour(traderAgent, toSend);
        final Event traderEvent = new Event(-1, traderBehaviour);
        traderAgentController.start();
        traderAgentController.putO2AObject(traderEvent, AgentController.ASYNC);
        traderEvent.waitUntilProcessed();

        assertThat(traderBehaviour.failed.isEmpty(), is(true));
        assertThat(traderBehaviour.received.size(), is(1));
        final ExecutionReport executionReport = traderBehaviour.received.get(newOrder);
        assertThat(executionReport.getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(executionReport.getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(executionReport.getLeavesQty().getValue(), is(newOrder.getOrderQty().getValue()));
        assertThat(executionReport.getExecType().getValue(), is(ExecType.NEW));
        assertThat(executionReport.getOrderID().getValue(), is(newOrder.getClOrdID().getValue()));
        assertThat(executionReport.getOrdStatus().getValue(), is(OrdStatus.NEW));
        assertThat(executionReport.getSide().getValue(), is(newOrder.getSide().getValue()));
        assertThat(executionReport.getSymbol().getValue(), is(newOrder.getSymbol().getValue()));
    }
}
