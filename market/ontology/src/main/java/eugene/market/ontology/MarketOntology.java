/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.SessionStatus;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.TradeID;
import eugene.market.ontology.internal.ExtendedSchema;
import eugene.market.ontology.internal.LEAPCodec;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.content.abs.AbsExtendedPrimitive;
import jade.content.abs.AbsObject;
import jade.content.lang.Codec;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;

import java.math.BigDecimal;

/**
 * Defines the Market Ontology used to send messages between Agents and the Market.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class MarketOntology extends BeanOntology {

    /**
     * Language for this {@link MarketOntology}.
     */
    public static final String LANGUAGE = getCodec().getName();

    /**
     * Singleton instance of {@link MarketOntology}.
     */
    private static final MarketOntology INSTANCE = new MarketOntology();

    /**
     * Name of this ontology.
     */
    public static final String NAME = "market-ontology";

    /**
     * Private constructor.
     */
    private MarketOntology() {
        super(NAME);

        try {
            add(new ExtendedSchema(BigDecimal.class), BigDecimal.class);
            add(AvgPx.class, false);
            add(ClOrdID.class, false);
            add(CumQty.class, false);
            add(TradeID.class, false);
            add(ExecType.class, false);
            add(LastPx.class, false);
            add(LastQty.class, false);
            add(LeavesQty.class, false);
            add(OrderID.class, false);
            add(OrderQty.class, false);
            add(OrdStatus.class, false);
            add(OrdType.class, false);
            add(Price.class, false);
            add(SessionStatus.class, false);
            add(Side.class, false);
            add(Symbol.class, false);

            add(ExecutionReport.class, false);
            add(Logon.class, false);
            add(NewOrderSingle.class, false);
            add(OrderCancelReject.class, false);
            add(OrderCancelRequest.class, false);

            add(AddOrder.class, false);
            add(DeleteOrder.class, false);
            add(OrderExecuted.class, false);
        }
        catch (OntologyException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object toObject(AbsObject abs, String lcType, Ontology referenceOnto)
            throws UngroundedException, OntologyException {
        if (abs == null) {
            return null;
        }

        // PRIMITIVE
        if (abs.getAbsType() == AbsExtendedPrimitive.ABS_EXTENDED_PRIMITIVE || abs instanceof AbsExtendedPrimitive) {
            return ((AbsExtendedPrimitive) abs).get();
        }

        return super.toObject(abs, lcType, referenceOnto);
    }

    protected AbsObject fromObject(Object obj, Ontology referenceOnto) throws OntologyException {
        if (obj == null) {
            return null;
        }

        if (obj instanceof BigDecimal) {
            return AbsExtendedPrimitive.wrap(obj);
        }
        return super.fromObject(obj, referenceOnto);
    }

    /**
     * Gets an instance of a {@link Codec} that should be used with {@link MarketOntology}.
     *
     * @return instance of {@link Codec}.
     */

    public static Codec getCodec() {
        return new LEAPCodec();
    }

    /**
     * Returns the singleton instance of this {@link MarketOntology}.
     *
     * @return Singleton instance of this {@link MarketOntology}.
     */
    public static Ontology getInstance() {
        return INSTANCE;
    }
}
