package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.SessionStatus;
import eugene.market.ontology.message.Logon;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Sends the {@link Logon} message to the {@link MarketAgent}. If the logon is successful,
 * the returned {@link Logon} message is routed to the {@link Application} and the behaviour adds {@link
 * MessageRoutingBehaviour} for execution by the Agent and finishes with {@link BehaviourResult#SUCCESS}. Otherwise,
 * it finishes with {@link BehaviourResult#FAILURE}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class LogonBehaviour extends SequentialBehaviour {

    private final BehaviourResult result = new BehaviourResult();

    /**
     * Creates a {@link LogonBehaviour} that will logon with this <code>marketAgent</code>.
     *
     * @param agent   {@link Agent} that will execute this {@link LogonBehaviour}.
     * @param session owner of this {@link LogonBehaviour}.
     */
    public LogonBehaviour(final Agent agent, final Session session) {
        super(agent);
        checkNotNull(agent);
        checkNotNull(session);

        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(session.getSymbol()));
        final ACLMessage logonRequest = session.aclRequest(logon);
        addSubBehaviour(new AchieveREInitiator(myAgent, logonRequest) {
            @Override
            public void handleInform(final ACLMessage inform) {
                final Logon logon = session.extractMessage(inform, Logon.class);
                if (null != logon && SessionStatus.SESSION_ACTIVE.field().equals(logon.getSessionStatus())) {
                    result.success();
                    session.getApplication().onLogon(logon, myAgent, session);
                    myAgent.addBehaviour(new MessageRoutingBehaviour(myAgent, session));
                }
            }
        });
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
