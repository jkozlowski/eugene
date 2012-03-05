/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client;

import org.testng.annotations.Test;

import static eugene.market.client.TopOfBookApplication.NO_PRICE;

/**
 * Tests {@link TopOfBookApplication#NO_PRICE}
 * 
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class TopOfBookApplicationTest {
    
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetPrice() {
        NO_PRICE.getPrice();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetSide() {
        NO_PRICE.getSide();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testNextPrice() {
        NO_PRICE.nextPrice(1);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testPrevPrice() {
        NO_PRICE.prevPrice(1);
    }
}
