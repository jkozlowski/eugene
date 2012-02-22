/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.esma.impl;

import eugene.market.book.Order;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.message.Logon;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maintains the mapping between <code>{@link Order}s</code> and ({@link AID}, {@link ClOrdID}) pairs and a list of
 * {@link AID}s of traders that sent a {@link Logon} message.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class Repository {

    private final Map<Order, Tuple> orderTupleMap = new HashMap<Order, Tuple>();

    private final Map<Tuple, Order> tupleOrderMap = new HashMap<Tuple, Order>();

    private final List<AID> aids = new LinkedList<AID>();

    private final List<AID> publicAIDs = Collections.unmodifiableList(aids);

    /**
     * Adds this <code>aclMessage</code>.
     *
     * @param aid aclMessage to add.
     */
    public synchronized void add(final AID aid) {
        checkNotNull(aid);
        aids.add(aid);
    }

    /**
     * Gets the list of aids.
     *
     * @return the list of aids.
     */
    public synchronized List<AID> getAIDs() {
        return publicAIDs;
    }

    /**
     * Puts this (<code>order</code>, <code>tuple</code>) pair into this {@link Repository}.
     *
     * @param order {@link Order} to put.
     * @param tuple {@link Tuple} to put.
     */
    public synchronized void put(final Order order, final Tuple tuple) {
        checkNotNull(order);
        checkNotNull(tuple);
        orderTupleMap.put(order, tuple);
        tupleOrderMap.put(tuple, order);
    }

    /**
     * Gets the {@link Tuple} for this <code>order</code>.
     *
     * @param order {@link Order} to get the {@link Tuple} for.
     *
     * @return {@link Tuple} for this <code>order</code>.
     */
    public synchronized Tuple get(final Order order) {
        return orderTupleMap.get(order);
    }

    /**
     * Gets the {@link Order} for this <code>tuple</code>.
     *
     * @param tuple {@link Tuple} to get the {@link Order} for.
     *
     * @return {@link Order} for this <code>tuple</code>.
     */
    public synchronized Order get(final Tuple tuple) {
        return tupleOrderMap.get(tuple);
    }

    /**
     * ({@link ACLMessage}, {@link ClOrdID}) pair.
     */
    public static final class Tuple {

        private final ACLMessage aclMessage;
        private final String clOrdID;

        public Tuple(final ACLMessage aclMessage, final String clOrdID) {
            checkNotNull(aclMessage);
            checkNotNull(clOrdID);
            checkArgument(!clOrdID.isEmpty());
            this.aclMessage = aclMessage;
            this.clOrdID = clOrdID;
        }

        /**
         * Gets the aclMessage.
         *
         * @return the aclMessage.
         */
        public ACLMessage getACLMessage() {
            return aclMessage;
        }

        /**
         * Gets the clOrdID.
         *
         * @return the clOrdID.
         */
        public String getClOrdID() {
            return clOrdID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Tuple tuple = (Tuple) o;

            if (!aclMessage.getSender().equals(tuple.aclMessage.getSender())) return false;
            if (!clOrdID.equals(tuple.clOrdID)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = aclMessage.getSender().hashCode();
            result = 31 * result + clOrdID.hashCode();
            return result;
        }
    }
}
