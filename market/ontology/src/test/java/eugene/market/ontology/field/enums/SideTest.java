package eugene.market.ontology.field.enums;

import eugene.market.ontology.message.NewOrderSingle;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Side}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SideTest {

    @Test
    public void testField() {
        assertThat(Side.BUY.field(), is(new eugene.market.ontology.field.Side(eugene.market.ontology.field
                                                                                              .Side.BUY)));
        assertThat(Side.SELL.field(), is(new eugene.market.ontology.field.Side(eugene.market.ontology.field
                                                                                      .Side.SELL)));
    }
    
    @Test
    public void testIsBuy() {
        assertThat(Side.BUY.isBuy(), is(true));
        assertThat(Side.SELL.isBuy(), is(false));
    }
    
    @Test
    public void testIsSell() {
        assertThat(Side.BUY.isSell(), is(false));
        assertThat(Side.SELL.isSell(), is(true));
    }

    @Test
    public void testGetOpposite() {
        assertThat(Side.BUY.getOpposite(), is(Side.SELL));
        assertThat(Side.SELL.getOpposite(), is(Side.BUY));
    }
    
    @Test
    public void testParse() {
        final eugene.market.ontology.field.Side side = new eugene.market.ontology.field.Side();
        side.setValue(eugene.market.ontology.field.Side.BUY);
        assertThat(Side.parse(side), is(Side.BUY));
        side.setValue(eugene.market.ontology.field.Side.SELL);
        assertThat(Side.parse(side), is(Side.SELL));
    }

    @Test
    public void getSideBuy() {
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setSide(new eugene.market.ontology.field.Side(eugene.market.ontology.field.Side.BUY));
        assertThat(Side.getSide(newOrderSingle), is(Side.BUY));
    }

    @Test
    public void getSideSell() {
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setSide(new eugene.market.ontology.field.Side(eugene.market.ontology.field.Side.SELL));
        assertThat(Side.getSide(newOrderSingle), is(Side.SELL));
    }
}