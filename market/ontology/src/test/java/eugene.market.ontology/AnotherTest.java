package eugene.market.ontology;

import jade.core.*;
import jade.core.Runtime;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * @author Jakub D Kozlowski
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class AnotherTest {

//    @Inject
//    private JadeRuntimeService jadeRuntimeService = null;

    @Configuration
    public static Option[] configure() {
        return options(
                junitBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.log").version("1.0.0"),
                mavenBundle().groupId("jade").artifactId("jade-osgi").versionAsInProject().start()
        );
    }

    @Test
    public void test() throws StaleProxyException, InterruptedException, ProfileException {

        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();

        // Check whether a '-container' flag was given

        // Create a default profile
        Profile p = new ProfileImpl(true);
        p.setParameter(Profile.NO_MTP, "true");
        p.setParameter(Profile.DETECT_MAIN, "false");
        p.setParameter(Profile.LOCAL_SERVICE_MANAGER, "false");
        p.setParameter(Profile.NO_DISPLAY, "true");
        System.out.println(p.getSpecifiers(Profile.IMTP));


        // Create a new non-main container, connecting to the default
        // main container (i.e. on this host, port 1099)
        System.out.println("Launching the agent container ..." + p);
        AgentContainer ac = rt.createMainContainer(p);

        // Create a new agent, a DummyAgent
        AgentController dummy = ac.createNewAgent("inProcess", "jade.tools.DummyAgent.DummyAgent", new Object[0]);

        // Fire up the agent
        System.out.println("Starting up a DummyAgent...");
        dummy.start();

        // Kill the DummyAgent
        System.out.println("Killing DummyAgent...");
        dummy.kill();

        ac.kill();
    }

    @Test
    public void test2() throws StaleProxyException, InterruptedException, ProfileException {

        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();

        // Check whether a '-container' flag was given

        // Create a default profile
        Profile p = new ProfileImpl(true);
        p.setParameter(Profile.NO_MTP, "true");
        p.setParameter(Profile.DETECT_MAIN, "false");
        p.setParameter(Profile.LOCAL_SERVICE_MANAGER, "false");
        p.setParameter(Profile.NO_DISPLAY, "true");
        System.out.println(p.getSpecifiers(Profile.IMTP));


        // Create a new non-main container, connecting to the default
        // main container (i.e. on this host, port 1099)
        System.out.println("Launching the agent container ..." + p);
        AgentContainer ac = rt.createMainContainer(p);

        // Create a new agent, a DummyAgent
        AgentController dummy = ac.createNewAgent("inProcess", "jade.tools.DummyAgent.DummyAgent", new Object[0]);

        // Fire up the agent
        System.out.println("Starting up a DummyAgent...");
        dummy.start();

        // Kill the DummyAgent
        System.out.println("Killing DummyAgent...");
        dummy.kill();

        ac.kill();
    }

}
