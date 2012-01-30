package eugene.agent.noise;

import eugene.market.book.OrderBook;
import eugene.market.book.OrderBooks;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.client.Sessions;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static eugene.market.client.Applications.orderBook;
import static eugene.market.client.Applications.proxy;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.core.Runtime.instance;

/**
 * Tests {@link NoiseTraderAgent} inside a JADE container.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class NoiseTraderAgentIntegrationTest {


    public static final String MARKET_AGENT = "market";

    public AgentController marketAgentController;

    public AgentContainer agentContainer;

    /**
     * Creates the container.
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        agentContainer = instance().createMainContainer(profile);

        initMarketAgent(agentContainer);
    }

    private void initMarketAgent(final AgentContainer agentContainer) throws StaleProxyException {
        final Agent marketAgent = new MarketAgent(defaultSymbol);
        marketAgentController = agentContainer.acceptNewAgent(MARKET_AGENT, marketAgent);
        marketAgentController.start();
    }

    @Test(enabled = true)
    public void testNoiseTraderAgent() throws StaleProxyException, InterruptedException {

        for (int i = 0; i < 150; i++) {
            final NoiseTraderAgent noiseTraderAgent = new NoiseTraderAgent(defaultSymbol);
            final AgentController traderAgentController = agentContainer.acceptNewAgent("noise-trader" + i,
                                                                                  noiseTraderAgent);
            traderAgentController.start();
        }

        for (int i = 0; i < 2; i++) {
            final GatewayAgent gatewayAgent = new GatewayAgent();
            gatewayAgent.getContentManager().registerLanguage(MarketOntology.getCodec(), MarketOntology.LANGUAGE);
            gatewayAgent.getContentManager().registerOntology(MarketOntology.getInstance());
            final AgentController gatewayAgentController = agentContainer.acceptNewAgent("orderBookPrinter" + i,
                                                                                        gatewayAgent);
            gatewayAgentController.start();
            final int myI = i;
            final OrderBook orderBook = OrderBooks.defaultOrderBook();
            final Behaviour behaviour = Sessions.initiate(gatewayAgent, proxy(orderBook(orderBook),
                new ApplicationAdapter() {
                    @Override
                    public void toApp(final AddOrder addOrder, final Session session) {
                        System.out.println("orderBookPrinter" + myI + 
                                           ", time=" + System.currentTimeMillis() + 
                                           "buySize=" + orderBook.size(Side.BUY) + 
                                           "buyPrice=" + (orderBook.isEmpty(Side.BUY) ? "NO-PRICE" : orderBook.peek
                                (Side.BUY).getPrice()) +
                                           "sellSize=" + orderBook.size(Side.SELL) +
                                           "sellPrice=" + (orderBook.isEmpty(Side.SELL) ? "NO-PRICE" : orderBook.peek
                                (Side.SELL).getPrice()));
                    }

                    @Override
                    public void toApp(final DeleteOrder deleteOrder, final Session session) {
                        System.out.println("orderBookPrinter" + myI +
                                                   ", time=" + System.currentTimeMillis() +
                                                   "buySize=" + orderBook.size(Side.BUY) +
                                                   "buyPrice=" + (orderBook.isEmpty(Side.BUY) ? "NO-PRICE" : orderBook.peek
                                (Side.BUY).getPrice()) +
                                                   "sellSize=" + orderBook.size(Side.SELL) +
                                                   "sellPrice=" + (orderBook.isEmpty(Side.SELL) ? "NO-PRICE" : orderBook.peek
                                (Side.SELL).getPrice()));
                    }

                    @Override
                    public void toApp(final OrderExecuted orderExecuted, final Session session) {
                        System.out.println("orderBookPrinter" + myI +
                                                   ", time=" + System.currentTimeMillis() +
                                                   "buySize=" + orderBook.size(Side.BUY) +
                                                   "buyPrice=" + (orderBook.isEmpty(Side.BUY) ? "NO-PRICE" : orderBook.peek
                                (Side.BUY).getPrice()) +
                                                   "sellSize=" + orderBook.size(Side.SELL) +
                                                   "sellPrice=" + (orderBook.isEmpty(Side.SELL) ? "NO-PRICE" : orderBook.peek
                                (Side.SELL).getPrice()));
                    }
            }), defaultSymbol);

            final Event traderEvent = new Event(-1, behaviour);
            gatewayAgentController.putO2AObject(traderEvent, AgentController.ASYNC);
        }

        Thread.sleep(30000L);
    }

}
