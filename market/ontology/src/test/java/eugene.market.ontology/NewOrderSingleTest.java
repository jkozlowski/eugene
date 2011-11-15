package eugene.market.ontology;

import eugene.market.ontology.message.NewOrderSingle;
import jade.osgi.service.runtime.JadeRuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.player.Player;
import org.ops4j.pax.exam.testforge.WaitForService;
import org.osgi.framework.BundleContext;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Tests {@link NewOrderSingle}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class NewOrderSingleTest {

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setup() throws Exception {

        new Player().with(
                options(
                        mavenBundle().groupId("jade").artifactId("jade-osgi").versionAsInProject().start()
                )
        )
        .test(WaitForService.class, JadeRuntimeService.class.getName(), 5000)
        .play();

        System.out.println("Got the bundle: " + bundleContext);
        System.out.println("Setup a container");
    }

    @Test
    public void test() {

    }


}
