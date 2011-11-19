package jade.imtp.memory;

import jade.core.BaseNode;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.ServiceException;

/**
 * Implementation of {@link Node} used byt {@link MemoryIMTPManager}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
// TODO: Improve this class to maybe do callbacks to ProfileManager.
public class MemoryNode extends BaseNode {

    public MemoryNode(String name, boolean hasPM) {
        super(name, hasPM);
    }

    /**
     * {@inheritDoc}
     */
    public Object accept(HorizontalCommand cmd) throws IMTPException {
        try {
            return super.serveHorizontalCommand(cmd);
        }
        catch (ServiceException se) {
            throw new IMTPException("Service Error", se);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean ping(boolean hang) throws IMTPException {
        return true; // Noop
    }

    /**
     * {@inheritDoc}
     */
    public void interrupt() throws IMTPException {

        // Noop
    }

    /**
     * {@inheritDoc}
     */
    public void exit() throws IMTPException {
        // Noop
    }
}
