/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.message;

import eugene.market.ontology.MarketOntology;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static jade.lang.acl.MessageTemplate.MatchLanguage;
import static jade.lang.acl.MessageTemplate.MatchOntology;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.and;

/**
 * @author Jakub D Kozlowski
 */
public class ReceiverBehaviour extends SimpleBehaviour {

    public final int toReceive;

    public final Set<Concept> received = Collections.synchronizedSet(new HashSet<Concept>());

    public final Set<ACLMessage> failed = Collections.synchronizedSet(new HashSet<ACLMessage>());

    final MessageTemplate template =
            and(MatchLanguage(MarketOntology.LANGUAGE), and(MatchOntology(MarketOntology.getInstance()
                                                                                  .getName()),
                                                            MatchPerformative(ACLMessage.REQUEST)));

    public ReceiverBehaviour(int toReceive) {
        this.toReceive = toReceive;
    }

    @Override
    public void action() {
        final ACLMessage msg = myAgent.blockingReceive(template);
        try {
            ContentElement ce = myAgent.getContentManager().extractContent(msg);

            if (Action.class != ce.getClass() && !(((Action) ce).getAction() instanceof Concept)) {
                failed.add(msg);
                return;
            }

            final Concept order = (Concept) ((Action) ce).getAction();
            received.add(order);
        }
        catch (Exception e) {
            failed.add(msg);
        }
    }

    public boolean done() {
        return received.size() + failed.size() >= toReceive;
    }
}
