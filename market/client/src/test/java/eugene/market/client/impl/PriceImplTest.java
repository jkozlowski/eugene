/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client.impl;

import eugene.market.client.TopOfBookApplication.Price;
import eugene.market.ontology.field.enums.Side;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link PriceImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class PriceImplTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new PriceImpl(null, Side.BUY, mock(Symbol.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroPrice() {
        new PriceImpl(BigDecimal.ZERO, Side.BUY, mock(Symbol.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSide() {
        new PriceImpl(BigDecimal.ONE, null, mock(Symbol.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new PriceImpl(BigDecimal.ONE, Side.BUY, null);
    }

    @Test
    public void testConstructor() {
        final Symbol symbol = mock(Symbol.class);
        final Price price = new PriceImpl(defaultPrice, Side.BUY, symbol);
        assertThat(price.getPrice(), is(defaultPrice));
        assertThat(price.getSide(), is(Side.BUY));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNextPriceZeroTicks() {
        final Price price = new PriceImpl(defaultPrice, Side.BUY, mock(Symbol.class));
        price.nextPrice(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNextPriceNegativeTicks() {
        final Price price = new PriceImpl(defaultPrice, Side.BUY, mock(Symbol.class));
        price.nextPrice(-1);
    }

    @Test
    public void testNextPriceBuySide() {
        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final Price price = new PriceImpl(defaultPrice, Side.BUY, symbol);

        final BigDecimal expected = defaultPrice.add(defaultTickSize.multiply(BigDecimal.ONE))
                                                .setScale(defaultTickSize.scale(), RoundingMode.HALF_UP);
        final BigDecimal actual = price.nextPrice(1);
        assertThat(actual, is(expected));
    }

    @Test
    public void testNextPriceSellSide() {
        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final Price price = new PriceImpl(defaultPrice, Side.SELL, symbol);

        final BigDecimal expected = defaultPrice.subtract(defaultTickSize.multiply(BigDecimal.ONE))
                .setScale(defaultTickSize.scale(), RoundingMode.HALF_UP);
        final BigDecimal actual = price.nextPrice(1);
        assertThat(actual, is(expected));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testPrevPriceZeroTicks() {
        final Price price = new PriceImpl(defaultPrice, Side.BUY, mock(Symbol.class));
        price.prevPrice(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testPrevPriceNegativeTicks() {
        final Price price = new PriceImpl(defaultPrice, Side.BUY, mock(Symbol.class));
        price.prevPrice(-1);
    }

    @Test
    public void testPrevPriceBuySide() {
        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final Price price = new PriceImpl(defaultPrice, Side.BUY, symbol);

        final BigDecimal expected = defaultPrice.subtract(defaultTickSize.multiply(BigDecimal.ONE))
                .setScale(defaultTickSize.scale(), RoundingMode.HALF_UP);
        final BigDecimal actual = price.prevPrice(1);
        assertThat(actual, is(expected));
    }

    @Test
    public void testPrevPriceSellSide() {
        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final Price price = new PriceImpl(defaultPrice, Side.SELL, symbol);

        final BigDecimal expected = defaultPrice.add(defaultTickSize.multiply(BigDecimal.ONE))
                .setScale(defaultTickSize.scale(), RoundingMode.HALF_UP);
        final BigDecimal actual = price.prevPrice(1);
        assertThat(actual, is(expected));
    }
}
