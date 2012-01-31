package eugene.simulation.agent.impl;

import eugene.simulation.ontology.LogonComplete;
import eugene.utils.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Waits for {@link LogonComplete} from the started {@link Agent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class ReceiveLogonCompleteMessages extends SimpleBehaviour {

    private final BehaviourResult<Set<String>> result = new BehaviourResult<Set<String>>();

    private final BehaviourResult<Set<String>> started;

    private final Set<String> logonComplete;

    public ReceiveLogonCompleteMessages(final BehaviourResult<Set<String>> started) {
        checkNotNull(started);
        this.started = started;
        this.logonComplete = Collections.synchronizedSet(new HashSet<String>());
    }

    @Override
    public void action() {
        final ACLMessage aclMessage = myAgent.receive();

        if (null == aclMessage) {
            return;
        }

        try {
            final ContentElement c = myAgent.getContentManager().extractContent(aclMessage);
            if (c instanceof Action && ((Action) c).getAction() instanceof LogonComplete) {
                logonComplete.add(aclMessage.getSender().getName());
            }
        }
        catch (CodecException e) {
            e.printStackTrace();
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the {@link BehaviourResult} used by this {@link ReceiveLogonCompleteMessages}.
     *
     * @return {@link BehaviourResult} used by this {@link ReceiveLogonCompleteMessages}.
     */
    public BehaviourResult<Set<String>> getResult() {
        return result;
    }

    @Override
    public boolean done() {
        if (started.getObject().equals(logonComplete)) {
            result.success(logonComplete);
            return true;
        }
        return false;
    }
}
