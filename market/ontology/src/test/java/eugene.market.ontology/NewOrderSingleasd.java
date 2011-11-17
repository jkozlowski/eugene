package eugene.market.ontology;

import eugene.market.ontology.message.NewOrderSingle;
import jade.jademx.agent.JademxAgent;
import jade.jademx.config.jademx.onto.ConfigAgentSpecifier;
import jade.jademx.config.jademx.onto.ConfigPlatform;
import jade.jademx.config.jademx.onto.ConfigRuntime;
import jade.jademx.config.jademx.onto.JademxConfig;
import jade.jademx.mbean.JadeFactory;
import jade.jademx.mbean.JadePlatformMBean;
import jade.jademx.mbean.JadeRuntimeMBean;
import jade.jademx.server.JadeMXServer;
import jade.jademx.server.JadeMXServerFactory;
import jade.jademx.util.AclMsgCmp;
import jade.jademx.util.ThrowableUtil;
import jade.osgi.service.runtime.JadeRuntimeService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Tests {@link eugene.market.ontology.message.NewOrderSingle}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
//@RunWith(JUnit4TestRunner.class)
public class NewOrderSingleasd {

    @Inject
    private BundleContext bundleContext = null;

    @Inject
    private JadeRuntimeService jadeRuntimeService = null;

    @Configuration
    public static Option[] configure() {
        return options(
                junitBundles(),
                systemProperty( "org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.log").version("1.0.0"),
                mavenBundle().groupId("jade").artifactId("jade-osgi").versionAsInProject().start(),
                mavenBundle().groupId("jade").artifactId("jademx").versionAsInProject()
        );
    }

    @Test
    public void test() {
        System.out.println(this.bundleContext);
    }

    @Test
    public void IHaveJADE() throws Exception {
        System.out.println("I gots JADE: ");
    }
}
