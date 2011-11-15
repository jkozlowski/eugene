package eugene.market.ontology;

import eugene.market.ontology.message.NewOrderSingle;
import jade.osgi.service.runtime.JadeRuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Tests {@link NewOrderSingle}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@RunWith(JUnit4TestRunner.class)
public class NewOrderSingleTest {

    @Inject
    private BundleContext bundleContext = null;

    @Inject
    private JadeRuntimeService jadeRuntimeService = null;

    @Configuration
    public static Option[] configure() {
        return options(
                mavenBundle().groupId("jade").artifactId("jade-osgi").versionAsInProject().start()
        );
    }

    @Test
    public void test() {
        System.out.println(this.bundleContext);
    }

    @Test
    public void IHaveJADE() throws Exception {
        System.out.println("I gots JADE: " + jadeRuntimeService.getContainerName());
    }
}
