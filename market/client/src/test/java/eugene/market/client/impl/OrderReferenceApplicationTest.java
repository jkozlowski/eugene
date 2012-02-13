package eugene.market.client.impl;

import eugene.market.client.Session;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

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
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));

        assertThat(application.getOrderReference(executionReport), sameInstance(orderReference));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullExecutionReport() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.getOrderReference(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.getOrderReference(new ExecutionReport());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceNullValueOfClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID());
        application.getOrderReference(executionReport);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetOrderReferenceInvalidClOrdID() {
        final OrderReferenceApplication application = new OrderReferenceApplication();
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        application.getOrderReference(executionReport);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testToAppRejectedExecutionReport() {
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);
        
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.REJECTED.field());
        
        application.toApp(executionReport, mock(Session.class));
        
        assertThat(orderReference.getOrdStatus().isRejected(), is(true));
        application.getOrderReference(executionReport);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testToAppFilledExecutionReport() {
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.FILLED.field());
        executionReport.setLastPx(new LastPx(defaultPrice));
        executionReport.setLastQty(new LastQty(defaultOrdQty));

        application.toApp(executionReport, mock(Session.class));

        assertThat(orderReference.getOrdStatus().isFilled(), is(true));
        application.getOrderReference(executionReport);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testToAppCanceledExecutionReport() {
        final OrderReferenceImpl orderReference = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L,
                                                                         OrdType.LIMIT, Side.BUY, defaultOrdQty,
                                                                         defaultPrice);
        final OrderReferenceApplication application = new OrderReferenceApplication();
        application.addOrderReference(orderReference);

        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(defaultClOrdID));
        executionReport.setOrdStatus(OrdStatus.CANCELED.field());

        application.toApp(executionReport, mock(Session.class));

        assertThat(orderReference.getOrdStatus().isCanceled(), is(true));
        application.getOrderReference(executionReport);
    }
}
