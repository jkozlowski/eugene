/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.book.Order;
import eugene.market.client.TopOfBookApplication.Price;
import eugene.market.client.impl.PriceImpl;
import eugene.market.ontology.Defaults;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.client.Messages.newLimit;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests {@link Messages}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class MessagesTest {

    private final Symbol defaultSymbol = Symbols.getSymbol(Defaults.defaultSymbol, defaultTickSize, defaultPrice);

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new Messages();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNewLimitNullSide() {
        newLimit(null, defaultPrice, defaultOrdQty);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNewLimitNullPrice() {
        newLimit(Side.BUY, (BigDecimal) null, defaultOrdQty);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNewLimitNullOrdQty() {
        newLimit(Side.BUY, defaultPrice, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNewLimitNoPrice() {
        newLimit(Side.BUY, Order.NO_PRICE, defaultOrdQty);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNewLimitNoQuantity() {
        newLimit(Side.BUY, defaultPrice, Order.NO_QTY);
    }

    @Test
    public void testNewLimit() {
        final NewOrderSingle newOrder = newLimit(Side.BUY, defaultPrice, defaultOrdQty);
        assertThat(newOrder.getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrder.getPrice().getValue(), is(defaultPrice));
        assertThat(newOrder.getClOrdID(), nullValue());
        assertThat(newOrder.getOrdType(), is(OrdType.LIMIT.field()));
        assertThat(newOrder.getSide(), is(Side.BUY.field()));
        assertThat(newOrder.getSymbol(), nullValue());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNewLimitPriceNullPrice() {
        newLimit((Price) null, defaultOrdQty);
    }

    @Test
    public void testNewLimitPrice() {
        final Price price = new PriceImpl(defaultPrice, Side.BUY, defaultSymbol);
        final NewOrderSingle newOrder = newLimit(price, defaultOrdQty);
        assertThat(newOrder.getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrder.getPrice().getValue(), is(defaultPrice));
        assertThat(newOrder.getClOrdID(), nullValue());
        assertThat(newOrder.getOrdType(), is(OrdType.LIMIT.field()));
        assertThat(newOrder.getSide(), is(Side.BUY.field()));
        assertThat(newOrder.getSymbol(), nullValue());
    }
}
