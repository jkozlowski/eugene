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
        assertThat(OrdStatus.NEW.field(),
                   is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field.OrdStatus.NEW)));
        
        assertThat(OrdStatus.CANCELED.field(),
                   is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field.OrdStatus.CANCELLED)));

        assertThat(OrdStatus.REJECTED.field(),
                   is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field.OrdStatus.REJECTED)));

        assertThat(OrdStatus.FILLED.field(),
                   is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field.OrdStatus.FILLED)));

        assertThat(OrdStatus.PARTIALLY_FILLED.field(),
                   is(new eugene.market.ontology.field.OrdStatus(eugene.market.ontology.field.OrdStatus.PARTIALLY_FILLED)));
    }

    @Test
    public void testIsNew() {
        assertThat(OrdStatus.NEW.isNew(), is(true));
        assertThat(OrdStatus.CANCELED.isNew(), is(false));
        assertThat(OrdStatus.REJECTED.isNew(), is(false));
        assertThat(OrdStatus.FILLED.isNew(), is(false));
        assertThat(OrdStatus.PARTIALLY_FILLED.isNew(), is(false));
    }
    
    @Test
    public void testIsCanceled() {
        assertThat(OrdStatus.NEW.isCanceled(), is(false));
        assertThat(OrdStatus.CANCELED.isCanceled(), is(true));
        assertThat(OrdStatus.REJECTED.isCanceled(), is(false));
        assertThat(OrdStatus.FILLED.isCanceled(), is(false));
        assertThat(OrdStatus.PARTIALLY_FILLED.isCanceled(), is(false));
    }

    @Test
    public void testIsRejected() {
        assertThat(OrdStatus.NEW.isRejected(), is(false));
        assertThat(OrdStatus.CANCELED.isRejected(), is(false));
        assertThat(OrdStatus.REJECTED.isRejected(), is(true));
        assertThat(OrdStatus.FILLED.isRejected(), is(false));
        assertThat(OrdStatus.PARTIALLY_FILLED.isRejected(), is(false));
    }



    @Test
    public void testIsFilled() {
        assertThat(OrdStatus.NEW.isFilled(), is(false));
        assertThat(OrdStatus.CANCELED.isFilled(), is(false));
        assertThat(OrdStatus.REJECTED.isFilled(), is(false));
        assertThat(OrdStatus.FILLED.isFilled(), is(true));
        assertThat(OrdStatus.PARTIALLY_FILLED.isFilled(), is(false));
    }

    @Test
    public void testIsPartiallyFilled() {
        assertThat(OrdStatus.NEW.isPartiallyFilled(), is(false));
        assertThat(OrdStatus.CANCELED.isPartiallyFilled(), is(false));
        assertThat(OrdStatus.REJECTED.isPartiallyFilled(), is(false));
        assertThat(OrdStatus.FILLED.isPartiallyFilled(), is(false));
        assertThat(OrdStatus.PARTIALLY_FILLED.isPartiallyFilled(), is(true));
    }
}
