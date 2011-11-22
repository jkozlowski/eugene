package eugene.market.esma;

import eugene.market.ontology.Message;
import eugene.market.ontology.message.NewOrderSingle;
import jade.lang.acl.ACLMessage;

import javax.validation.constraints.NotNull;

/**
 * Defines methods for accepting {@link Message}s for execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public interface MarketServer {

    /**
     * Accepts {@link NewOrderSingle} message for execution.
     *
     * @param newOrderSingle order to accept.
     * @param request        original {@link ACLMessage}.
     */
    public void serveNewOrderSingleRequest(@NotNull final NewOrderSingle newOrderSingle,
                                           @NotNull final ACLMessage request) throws
                                                                              RequestExecutionException;

    /**
     * Indicates that a request could not be completed.
     *
     * @author Jakub D Kozlowski
     * @since 0.2
     */
    public static class RequestExecutionException extends Exception {

        public RequestExecutionException() {
            this("Could not complete the request");
        }

        public RequestExecutionException(final String msg) {
            super(msg);
        }
    }
}
