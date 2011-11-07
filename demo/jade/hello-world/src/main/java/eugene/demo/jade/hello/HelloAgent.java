package eugene.demo.jade.hello;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * HelloAgent is an example implementation
 */
public final class HelloAgent extends Agent {
	
	public void setup() {
		System.out.println("Hello. My name is " + getLocalName());
		
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				final ACLMessage msgRx = receive();
				if (msgRx != null) {
					System.out.println(msgRx);
					ACLMessage msgTx = msgRx.createReply();
					msgTx.setContent("Hello " + msgRx.getSender().getLocalName() + "!");
					send(msgTx);
				} else {
					block();
				}
			}
		});
	}
	
}
