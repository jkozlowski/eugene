package eugene.market.ontology;

import jade.core.*;
import jade.core.Service.Slice;
import jade.mtp.TransportAddress;
import jade.util.leap.List;

/**
 * @author Jakub D Kozlowski
 */
public class MyIMTPManager implements IMTPManager {

    public MyIMTPManager() {
        System.out.println("I have been instantiated bitches!!!");
    }

    /**
     * Initialize this IMTPManager
     */
    @Override
    public void initialize(Profile p) throws IMTPException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Release all resources of this IMTPManager
     */
    @Override
    public void shutDown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Access the node that represents the local JVM.
     *
     * @return A <code>Node</code> object, representing the local node
     *         of this platform.
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     */
    @Override
    public Node getLocalNode() throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Makes the platform <i>Service Manager</i> available through
     * this IMTP.
     *
     * @param mgr The <code>ServiceManager</code> implementation that
     *            is to be made available across the network.
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     */
    @Override
    public void exportPlatformManager(PlatformManager mgr) throws IMTPException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Stops making the platform <i>Service Manager</i> available
     * through this IMTP.
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     */
    @Override
    public void unexportPlatformManager(PlatformManager sm) throws IMTPException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Retrieve a proxy to the PlatformManager specified in the local
     * Profile
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     */
    @Override
    public PlatformManager getPlatformManagerProxy() throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Retrieve a proxy to the PlatformManager listening at a given address
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     */
    @Override
    public PlatformManager getPlatformManagerProxy(String addr) throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Inform the local IMTPManager that this node is now connected to
     * the given PlatformManager
     */
    @Override
    public void reconnected(PlatformManager pm) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Builds a proxy object for a remote service slice.
     *
     * @param itfs The array of all the interfaces that have to be
     *             implemented by the returned proxy. The first element of the
     *             array must be an interface derived from
     *             <code>Service.Slice</code>.
     *
     * @return A proxy object that can be safely casted to any of the
     *         interfaces in the <code>itfs</code> array.
     *
     * @throws IMTPException If something goes wrong in the underlying
     *                       network transport.
     * @see Service
     */
    @Override
    public Slice createSliceProxy(String serviceName, Class itf, Node where) throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Return the the List of TransportAddress where this IMTP is
     * waiting for intra-platform remote calls.
     */
    @Override
    public List getLocalAddresses() throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     */
    @Override
    public TransportAddress stringToAddr(String addr) throws IMTPException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
