package eugene.market.ontology.field.enums;

import eugene.market.ontology.message.NewOrderSingle;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link OrdType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class OrdTypeTest {

    @Test
    public void testField() {
        assertThat(OrdType.LIMIT.field(), is(new eugene.market.ontology.field.OrdType(eugene.market.ontology.field
                                                                                              .OrdType.LIMIT)));
        assertThat(OrdType.MARKET.field(), is(new eugene.market.ontology.field.OrdType(eugene.market.ontology.field
                                                                                               .OrdType.MARKET)));
    }
    
    @Test
    public void testIsLimit() {
        assertThat(OrdType.LIMIT.isLimit(), is(true));
        assertThat(OrdType.MARKET.isLimit(), is(false));
    }

    @Test
    public void testIsMarket() {
        assertThat(OrdType.LIMIT.isMarket(), is(false));
        assertThat(OrdType.MARKET.isMarket(), is(true));
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrdTypeNullNewOrderSingle() {
        OrdType.getOrdType(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrdTypeNullOrdType() {
        OrdType.getOrdType(new NewOrderSingle());
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetOrdTypeIllegalValue() {
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setOrdType(new eugene.market.ontology.field.OrdType("Hello World!"));
        OrdType.getOrdType(newOrderSingle);
    }
    
    @Test
    public void testGetOrdTypeLimit() {
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setOrdType(new eugene.market.ontology.field.OrdType(eugene.market.ontology.field.OrdType.LIMIT));
        assertThat(OrdType.getOrdType(newOrderSingle), is(OrdType.LIMIT));
    }

    @Test
    public void testGetOrdTypeMarket() {
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setOrdType(new eugene.market.ontology.field.OrdType(eugene.market.ontology.field.OrdType.MARKET));
        assertThat(OrdType.getOrdType(newOrderSingle), is(OrdType.MARKET));
    }
}
