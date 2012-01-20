package eugene.market.client.impl.application;

import com.google.common.base.Function;
import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

/**
 * Implementation of {@link Application} that can proxy {@link Message}s to other {@link Application}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class ProxyApplication implements Application {

    private final List<Application> applications;

    /**
     * Creates a {@link ProxyApplication} that will proxy {@link Message}s to these <code>applications</code>.
     *
     * @param applications {@link Application}s to proxy {@link Message}s to.
     */
    public ProxyApplication(final Application... applications) {
        checkNotNull(applications);
        checkArgument(0 != applications.length);
        this.applications = copyOf(applications);
    }

    @Override
    public void onLogon(final Logon logon, final Agent agent, final Session session) {
        forAll(new Function<Application, Void>() {
            @Override
            public Void apply(final Application application) {
                application.onLogon(logon, agent, session);
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

    private void forAll(final Function<Application, Void> function) {
        for (final Application application : applications) {
            function.apply(application);
        }
    }
}
