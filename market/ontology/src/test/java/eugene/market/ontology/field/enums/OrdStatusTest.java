package eugene.market.ontology.field.enums;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link OrdStatus}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class OrdStatusTest {

    @Test
    public void testField() {
        assertThat(OrdStatus.NEW.field(), is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field
                                                                                              .OrdStatus.NEW)));
        assertThat(OrdStatus.CANCELED.field(), is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field
                                                                                                .OrdStatus.CANCELLED)));
        assertThat(OrdStatus.REJECTED.field(), is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field
                                                                                                .OrdStatus.REJECTED)));
        assertThat(OrdStatus.FILLED.field(), is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field
                                                                                                .OrdStatus.FILLED)));
        assertThat(OrdStatus.PARTIALLY_FILLED.field(), is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field
                                                                                                .OrdStatus.PARTIALLY_FILLED)));
    }
}
