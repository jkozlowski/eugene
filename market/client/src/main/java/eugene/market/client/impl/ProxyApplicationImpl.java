package eugene.market.client.impl;

import com.google.common.base.Function;
import eugene.market.client.Application;
import eugene.market.client.ProxyApplication;
import eugene.market.client.Session;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of {@link Application} that can proxy {@link Message}s to other {@link Application}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class ProxyApplicationImpl implements ProxyApplication {

    private final CopyOnWriteArrayList<Application> applications;

    /**
     * Creates a {@link ProxyApplicationImpl} that will proxy {@link Message}s to these <code>applications</code>.
     *
     * @param applications {@link Application}s to proxy {@link Message}s to.
     */
    public ProxyApplicationImpl(final Application... applications) {
        checkNotNull(applications);
        checkArgument(0 != applications.length);
        this.applications = new CopyOnWriteArrayList<Application>(applications);
    }

    @Override
    public void onStart(final Start start, final Agent agent, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.onStart(start, agent, session);
                return null;
            }
        });
    }

    @Override
    public void onStop(final Stop stop, final Agent agent, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.onStop(stop, agent, session);
                return null;
            }
        });
    }

    @Override
    public void toApp(final ExecutionReport executionReport, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.toApp(executionReport, session);
                return null;
            }
        });
    }

    @Override
    public void toApp(final OrderCancelReject orderCancelReject, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.toApp(orderCancelReject, session);
                return null;
            }
        });
    }

    @Override
    public void toApp(final AddOrder addOrder, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.toApp(addOrder, session);
                return null;
            }
        });
    }

    @Override
    public void toApp(final DeleteOrder deleteOrder, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.toApp(deleteOrder, session);
                return null;
            }
        });
    }

    @Override
    public void toApp(final OrderExecuted orderExecuted, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.toApp(orderExecuted, session);
                return null;
            }
        });
    }

    @Override
    public void fromApp(final NewOrderSingle newOrderSingle, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.fromApp(newOrderSingle, session);
                return null;
            }
        });
    }

    @Override
    public void fromApp(final OrderCancelRequest orderCancelRequest, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.fromApp(orderCancelRequest, session);
                return null;
            }
        });
    }

    private void forAll(final Function<Application, Void> function) {
        for (final Application application : applications) {
            function.apply(application);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addApplication(Application application) {
        return applications.addIfAbsent(application);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeApplication(Application application) {
        return applications.remove(application);
    }
}
