/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.OrderReferenceListener;
import eugene.market.client.Session;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import org.testng.annotations.Test;

import static eugene.market.client.OrderReferenceListener.EMPTY_LISTENER;
import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests {@link OrderReferenceApplication}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OrderReferenceApplicationTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddOrderReferenceNullOrderReference() {
        new OrderReferenceApplication().addOrderReference(null);
    }

    @Test
    public void testAddOrderReferenceGetOrderReference() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(EMPTY_LISTENER, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));

        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), sameInstance(orderReference));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullExecutionReport() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.getOrderReference(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.getOrderReference(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullValueOfClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.getOrderReference(new ClOrdID());
    }

    @Test
    public void testGetOrderReferenceInvalidClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), nullValue());
    }

    @Test
    public void testToAppRejectedExecutionReport() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.REJECTED.field());

        final Session session = mock(Session.class);

        application.toApp(executionReport, session);

        assertThat(orderReference.getOrdStatus().isRejected(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), nullValue());

        verify(listener).rejectedEvent(executionReport, orderReference, session);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testToAppNewExecutionReport() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.NEW.field());

        final Session session = mock(Session.class);

        application.toApp(executionReport, session);

        assertThat(orderReference.getOrdStatus().isNew(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), sameInstance(orderReference));

        verify(listener).newEvent(executionReport, orderReference, session);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testToAppPartiallyFilledExecutionReport() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.PARTIALLY_FILLED.field());
        executionReport.setLastPx(new LastPx(defaultPrice));
        executionReport.setLastQty(new LastQty(defaultOrdQty-1L));

        final Session session = mock(Session.class);

        application.toApp(executionReport, session);

        assertThat(orderReference.getOrdStatus().isPartiallyFilled(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), sameInstance(orderReference));

        verify(listener).tradeEvent(executionReport, orderReference, session);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testToAppFilledExecutionReport() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.FILLED.field());
        executionReport.setLastPx(new LastPx(defaultPrice));
        executionReport.setLastQty(new LastQty(defaultOrdQty));

        final Session session = mock(Session.class);

        application.toApp(executionReport, session);

        assertThat(orderReference.getOrdStatus().isFilled(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), nullValue());

        verify(listener).tradeEvent(executionReport, orderReference, session);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testToAppCanceledExecutionReport() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.CANCELED.field());

        final Session session = mock(Session.class);

        application.toApp(executionReport, session);

        assertThat(orderReference.getOrdStatus().isCanceled(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), nullValue());

        verify(listener).canceledEvent(executionReport, orderReference, session);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testToAppOrderCancelReject() {
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(listener, defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        orderCancelReject.setClOrdID(new ClOrdID(defaultClOrdID));
        orderCancelReject.setOrdStatus(OrdStatus.CANCELED.field());

        final Session session = mock(Session.class);

        application.toApp(orderCancelReject, session);

        assertThat(orderReference.getOrdStatus().isNew(), is(true));
        assertThat(application.getOrderReference(new ClOrdID(defaultClOrdID)), sameInstance(orderReference));

        verify(listener).cancelRejectedEvent(orderCancelReject, orderReference, session);
        verifyNoMoreInteractions(listener);
    }
}
