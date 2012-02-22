/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

/**
 * Default implementation of {@link VwapExecution}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapExecutionImpl implements VwapExecution {

    private final Long quantity;

    private final Side side;

    private final List<BigDecimal> targets;

    /**
     * Default constructor.
     *
     * @param quantity quantity to trade.
     * @param side     side to trade.
     * @param targets  volume targets.
     *
     * @throws NullPointerException     if either argument is null.
     * @throws IllegalArgumentException if <code>quantity <= 0</code>.
     * @throws IllegalArgumentException if <code>targets.length <= 0</code>.
     * @throws IllegalArgumentException if sum of targets is not 1.
     */
    public VwapExecutionImpl(final Long quantity, final Side side, final BigDecimal... targets) {
        checkNotNull(quantity);
        checkArgument(quantity > 0);
        checkNotNull(side);
        checkNotNull(targets);
        checkArgument(targets.length > 0);

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal target : targets) {
            checkNotNull(target);
            sum = sum.add(target);
        }
        checkArgument(sum.compareTo(BigDecimal.ONE) == 0);

        this.quantity = quantity;
        this.side = side;
        this.targets = unmodifiableList(newArrayList(targets));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getQuantity() {
        return quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Side getSide() {
        return side;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getTargets() {
        return targets;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("VwapExecutionImpl");
        sb.append("[quantity=").append(quantity);
        sb.append(", side=").append(side);
        sb.append(", targets=").append(targets);
        sb.append(']');
        return sb.toString();
    }
}
