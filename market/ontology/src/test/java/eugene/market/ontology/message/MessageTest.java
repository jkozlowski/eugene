/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.message;

import eugene.market.ontology.Defaults;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.SessionStatus;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.TradeID;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.content.Concept;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static eugene.market.ontology.Defaults.defaultAvgPx;
import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultCumQty;
import static eugene.market.ontology.Defaults.defaultLastPx;
import static eugene.market.ontology.Defaults.defaultLastQty;
import static eugene.market.ontology.Defaults.defaultLeavesQty;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultOrderID;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.core.Runtime.instance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Base class for testing {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MessageTest {


    @Test
    public void testSendMessages() throws InterruptedException, ControllerException,
                                                        IllegalAccessException {

        final AgentContainer container = getContainer();

        final Set<Concept> toSend = getMessages();

        final ReceiverBehaviour receiverBehaviour = new ReceiverBehaviour(toSend.size());
        final Event receiverEvent = new Event(-1, receiverBehaviour);
        final AgentController receiverController = container.getAgent(Defaults.RECEIVER_AGENT);
        receiverController.start();
        receiverController.putO2AObject(receiverEvent, AgentController.ASYNC);

        final SenderBehaviour senderBehaviour = new SenderBehaviour(toSend);
        final Event senderEvent = new Event(-1, senderBehaviour);
        final AgentController senderController = container.getAgent(Defaults.SENDER_AGENT);
        senderController.start();
        senderController.putO2AObject(senderEvent, AgentController.ASYNC);
        senderEvent.waitUntilProcessed();

        assertThat(senderBehaviour.failed.isEmpty(), is(true));
        assertThat(senderBehaviour.sent, is(toSend));

        receiverEvent.waitUntilProcessed();

        assertThat(receiverBehaviour.failed.isEmpty(), is(true));
        assertThat(receiverBehaviour.received, is(toSend));

        container.kill();
    }

    public static Set<Concept> getMessages() {
        final Set<Concept> messages = Collections.synchronizedSet(new HashSet<Concept>());

        // Logon
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(defaultSymbol));
        logon.setSessionStatus(new SessionStatus(SessionStatus.SESSION_ACTIVE));
        messages.add(logon);

        final Logon logonNoSessionStatus = new Logon();
        logonNoSessionStatus.setSymbol(new Symbol(defaultSymbol));
        messages.add(logonNoSessionStatus);

        // ExecutionReport
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setExecType(new ExecType(ExecType.NEW));
        executionReport.setSymbol(new Symbol(defaultSymbol));
        executionReport.setSide(new Side(Side.BUY));
        executionReport.setLeavesQty(new LeavesQty(defaultLeavesQty));
        executionReport.setCumQty(new CumQty(defaultCumQty));
        executionReport.setAvgPx(new AvgPx(defaultAvgPx));
        executionReport.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        executionReport.setOrderID(new OrderID(defaultOrderID));
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        messages.add(executionReport);

        final ExecutionReport executionReportNoLastPx = new ExecutionReport();
        executionReportNoLastPx.setExecType(new ExecType(ExecType.NEW));
        executionReportNoLastPx.setSymbol(new Symbol(defaultSymbol));
        executionReportNoLastPx.setSide(new Side(Side.BUY));
        executionReportNoLastPx.setLeavesQty(new LeavesQty(defaultLeavesQty));
        executionReportNoLastPx.setLastQty(new LastQty(defaultLastQty));
        executionReportNoLastPx.setCumQty(new CumQty(defaultCumQty));
        executionReportNoLastPx.setAvgPx(new AvgPx(defaultAvgPx));
        executionReportNoLastPx.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        executionReportNoLastPx.setOrderID(new OrderID(defaultOrderID));
        executionReportNoLastPx.setClOrdID(new ClOrdID(defaultClOrdID));
        messages.add(executionReportNoLastPx);

        final ExecutionReport executionReportNoLastQty = new ExecutionReport();
        executionReportNoLastQty.setExecType(new ExecType(ExecType.NEW));
        executionReportNoLastQty.setSymbol(new Symbol(defaultSymbol));
        executionReportNoLastQty.setSide(new Side(Side.BUY));
        executionReportNoLastQty.setLeavesQty(new LeavesQty(defaultLeavesQty));
        executionReportNoLastQty.setCumQty(new CumQty(defaultCumQty));
        executionReportNoLastQty.setAvgPx(new AvgPx(defaultAvgPx));
        executionReportNoLastQty.setLastPx(new LastPx(defaultLastPx));
        executionReportNoLastQty.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        executionReportNoLastQty.setOrderID(new OrderID(defaultOrderID));
        executionReportNoLastQty.setClOrdID(new ClOrdID(defaultClOrdID));
        messages.add(executionReportNoLastQty);

        // NewOrderSingle
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setPrice(new Price(Defaults.Price));
        newOrder.setSymbol(new Symbol(defaultSymbol));
        newOrder.setOrdType(new OrdType(OrdType.LIMIT));
        messages.add(newOrder);

        final NewOrderSingle newOrderNoPrice = new NewOrderSingle();
        newOrderNoPrice.setClOrdID(new ClOrdID(Defaults.defaultClOrdID));
        newOrderNoPrice.setSide(new Side(Side.BUY));
        newOrderNoPrice.setOrderQty(new OrderQty(defaultOrdQty));
        newOrderNoPrice.setSymbol(new Symbol(Defaults.defaultSymbol));
        newOrderNoPrice.setOrdType(new OrdType(OrdType.LIMIT));
        messages.add(newOrderNoPrice);

        // OrderCancelReject
        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        orderCancelReject.setClOrdID(new ClOrdID(Defaults.defaultClOrdID));
        orderCancelReject.setOrderID(new OrderID(Defaults.defaultOrderID));
        orderCancelReject.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        messages.add(orderCancelReject);

        // OrderCancelRequest
        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setClOrdID(new ClOrdID(Defaults.defaultClOrdID));
        orderCancelRequest.setSide(new Side(Side.BUY));
        orderCancelRequest.setOrderQty(new OrderQty(defaultOrdQty));
        orderCancelRequest.setSymbol(new Symbol(Defaults.defaultSymbol));
        messages.add(orderCancelRequest);

        // AddOrder
        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(Defaults.defaultOrderID));
        addOrder.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder.setPrice(new Price(Defaults.Price));
        addOrder.setSide(new Side(Side.BUY));
        addOrder.setSymbol(new Symbol(Defaults.defaultSymbol));
        messages.add(addOrder);

        // DeleteOrder
        final DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.setOrderID(new OrderID(Defaults.defaultOrderID));
        messages.add(deleteOrder);

        // OrderExecuted
        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setOrderID(new OrderID(Defaults.defaultOrderID));
        orderExecuted.setTradeID(new TradeID(Defaults.defaultTradeID));
        orderExecuted.setLastPx(new LastPx(Defaults.Price));
        orderExecuted.setLastQty(new LastQty(defaultOrdQty));
        orderExecuted.setLeavesQty(new LeavesQty(Defaults.defaultLeavesQty));
        messages.add(orderExecuted);

        return messages;
    }


    /**
     * Creates the container.
     */
    public static AgentContainer getContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        final AgentContainer agentContainer = instance().createMainContainer(profile);

        final GatewayAgent sender = new GatewayAgent();
        agentContainer.acceptNewAgent(Defaults.SENDER_AGENT, sender);
        initAgent(sender);

        final GatewayAgent receiver = new GatewayAgent();
        agentContainer.acceptNewAgent(Defaults.RECEIVER_AGENT, receiver);
        initAgent(receiver);

        return agentContainer;
    }

    /**
     * Initializes {@link Agent}'s language and ontology.
     *
     * @param a agent to initialize.
     */
    public static void initAgent(final Agent a) {
        a.getContentManager().registerLanguage(MarketOntology.getCodec(), MarketOntology.LANGUAGE);
        a.getContentManager().registerOntology(MarketOntology.getInstance());
    }
}
