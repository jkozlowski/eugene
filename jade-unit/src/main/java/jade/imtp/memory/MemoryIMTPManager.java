package jade.imtp.memory;

import jade.core.IMTPException;
import jade.core.IMTPManager;
import jade.core.Node;
import jade.core.PlatformManager;
import jade.core.Profile;
import jade.core.Service;
import jade.core.SliceProxy;
import jade.imtp.leap.LEAPIMTPManager;
import jade.imtp.rmi.RMIIMTPManager;
import jade.mtp.TransportAddress;
import jade.util.Logger;
import jade.util.leap.LinkedList;
import jade.util.leap.List;

/**
 * Implements a local memory IMTP manager for creating a platform of local in-memory containers. For now only allows
 * creation of Main-Containers.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MemoryIMTPManager implements IMTPManager {

    /**
     * {@link Profile} for this {@link MemoryIMTPManager}.
     */
    private Profile profile;

    /**
     * Unique id of this container. It is as unique as a concatenation of this {@link MemoryIMTPManager}'s {@link
     * Object#hashCode()} and {@link System#currentTimeMillis()} can be.
     */
    private String id;

    /**
     * {@link MemoryNode} of this {@link MemoryIMTPManager}.
     */
    private MemoryNode localNode;

    /**
     * {@link PlatformManager} of this {@link MemoryIMTPManager}.
     */
    private PlatformManager platformManager = null;

    private static final Logger LOG = Logger.getMyLogger(MemoryIMTPManager.class.getName());

    /**
     * {@inheritDoc}
     */
    public void initialize(final Profile p) throws IMTPException {
        assert null != p;
        this.profile = p;

        /**
         * Ignore {@link Profile#MAIN} property and creates a main container.
         *
         */
        this.profile.setParameter(Profile.MAIN, Boolean.TRUE.toString());

        /**
         * Generate the id of this {@link MemoryIMTPManager} and store it in the <code>profile</code>.
         *
         */
        this.id = Integer.toHexString(hashCode()) + Long.toHexString(System.currentTimeMillis());
        this.profile.setParameter(Profile.MAIN_PORT, this.id);
        this.profile.setParameter(Profile.LOCAL_PORT, this.id);

        /**
         * Store host name and protocol in the profile.
         *
         */
        this.profile.setParameter(Profile.MAIN_HOST, MemoryProfile.DEFAULT_HOST_NAME);
        this.profile.setParameter(Profile.MAIN_PROTO, MemoryProfile.PROTOCOL);
        this.profile.setParameter(Profile.LOCAL_HOST, MemoryProfile.DEFAULT_HOST_NAME);

        /**
         * Create a {@link MemoryNode} for this {@link MemoryIMTPManager}.
         *
         */
        this.localNode = new MemoryNode(PlatformManager.NO_NAME, Boolean.TRUE);

        final StringBuilder b = new StringBuilder("Listening for intra-platform commands on address:\n");
        b.append(MemoryProfile.PROTOCOL).append("://").append(MemoryProfile.DEFAULT_HOST_NAME).append(":").append(id);
        LOG.log(Logger.INFO, b.toString());
    }

    /**
     * {@inheritDoc}
     */
    public void shutDown() {
        // Noop
    }

    /**
     * {@inheritDoc}
     */
    public Node getLocalNode() throws IMTPException {
        return localNode;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    public void exportPlatformManager(PlatformManager platformManager) throws IMTPException {
        assert null == this.platformManager;
        this.platformManager = platformManager;
        this.platformManager.setLocalAddress(id);
    }

    /**
     * {@inheritDoc}
     */
    public void unexportPlatformManager(PlatformManager platformManager) throws IMTPException {
        if (null != getPlatformManagerProxy() && getPlatformManagerProxy().equals(platformManager)) {
            this.platformManager = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public PlatformManager getPlatformManagerProxy() throws IMTPException {
        return platformManager;
    }

    /**
     * {@inheritDoc}
     */
    public PlatformManager getPlatformManagerProxy(String addr) throws IMTPException {
        if (null != getPlatformManagerProxy() && getPlatformManagerProxy().getLocalAddress().equals(addr)) {
            return getPlatformManagerProxy();
        }
        throw new IMTPException("Can't get a proxy to the PlatformManager at address " + addr);
    }

    /**
     * {@inheritDoc}
     */
    public void reconnected(PlatformManager pm) {
        // Noop
    }

    /**
     * {@inheritDoc}
     *
     * @see LEAPIMTPManager#createSliceProxy(String, Class, Node)
     * @see RMIIMTPManager#createSliceProxy(String, Class, Node)
     */
    public Service.Slice createSliceProxy(String serviceName, Class itf, Node where) throws IMTPException {
        try {
            Class proxyClass = Class.forName(serviceName + "Proxy");
            Service.Slice proxy = (Service.Slice) proxyClass.newInstance();
            if (proxy instanceof SliceProxy) {
                ((SliceProxy) proxy).setNode(where);
            }
            //#DOTNET_EXCLUDE_BEGIN
            else if (proxy instanceof Service.SliceProxy) {
                ((Service.SliceProxy) proxy).setNode(where);
            }
            //#DOTNET_EXCLUDE_END
            else {
                throw new IMTPException("Class " + proxyClass.getName() + " is not a slice proxy.");
            }
            return proxy;
        }
        catch (Exception e) {
            throw new IMTPException("Error creating a slice proxy", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List getLocalAddresses() throws IMTPException {
        final List l = new LinkedList();
        System.out.println(id);
        final MemoryTransportAddress address = new MemoryTransportAddress(id, null, null);
        l.add(address);
        return l;
    }

    /**
     * {@inheritDoc}
     */
    // TODO: Implement parsing of the url.
    public TransportAddress stringToAddr(String url) throws IMTPException {

        if (null == url) {
            throw new IMTPException("Null URL");
        }

        return new MemoryTransportAddress(url, null, null);
    }
}
