package eugene.market.ontology;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;

/**
 * @author Jakub D Kozlowski
 */
public class AnotherTest {

    @Test
    public void setup() throws StaleProxyException, InterruptedException {
        // Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);

      // Check whether a '-container' flag was given

	  // Create a default profile
	  Profile p = new ProfileImpl(false);
	  //p.setParameter(Profile.MAIN, "false");

	  // Create a new non-main container, connecting to the default
	  // main container (i.e. on this host, port 1099)
      System.out.println("Launching the agent container ..."+p);
	  AgentContainer ac = rt.createMainContainer(p);

	  // Create a new agent, a DummyAgent
	  AgentController dummy = ac.createNewAgent("inProcess", "jade.tools.DummyAgent.DummyAgent", new Object[0]);

	  // Fire up the agent
	  System.out.println("Starting up a DummyAgent...");
	  dummy.start();

	  // Wait for 10 seconds
	  Thread.sleep(10000);

	  // Kill the DummyAgent
	  System.out.println("Killing DummyAgent...");
	  dummy.kill();

	  // Create another peripheral container within the same JVM
	  // NB. Two containers CAN'T share the same Profile object!!! -->
	  // Create a new one.
	  p = new ProfileImpl(false);
	  //p.putProperty(Profile.MAIN, "false");
	  AgentContainer another = rt.createMainContainer(p);

	  // Launch the Mobile Agent example
	  // and pass it 2 arguments: a String and an object reference
	  Object[] arguments = new Object[2];
	  arguments[0] = "Hello World!";
	  arguments[1]=dummy;
	  AgentController mobile = another.createNewAgent("Johnny", "examples.mobile.MobileAgent", arguments);
	  mobile.start();
    }

}
