/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.OrderReference;

import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Orders {@link OrderReference}s by {@link OrderReference#getCreationTime()}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public final class OrderReferenceComparator implements Comparator<OrderReference> {

    private static final Comparator<OrderReference> INSTANCE = new OrderReferenceComparator();

    /**
     * Singleton constructor.
     */
    private OrderReferenceComparator() {
    }

    @Override
    public int compare(OrderReference o1, OrderReference o2) {
        checkNotNull(o1);
        checkNotNull(o2);

        return (o1.getCreationTime().compareTo(o2.getCreationTime()));
    }

    /**
     * Gets the singleton instance of this {@link OrderReferenceComparator}.
     *
     * @return singleton instance.
     */
    public static Comparator<OrderReference> getInstance() {
        return INSTANCE;
    }
}
