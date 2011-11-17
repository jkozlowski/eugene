package eugene.market.ontology;

import jade.core.*;
import jade.core.Service.Slice;
import jade.security.JADESecurityException;

import java.util.Vector;

/**
 * @author Jakub D Kozlowski
 */
public class MyPlatformManager implements PlatformManager {

    @Override
    public String getPlatformName() throws IMTPException {
        return "bullshit";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getLocalAddress() {
        return "bullshit";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLocalAddress(String addr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param dsc        The Descriptor of the new Node
     * @param services   The services currently installed on the new Node
     * @param propagated Flag indicating whether the new-node event
     *                   was a propagated event within the replication mechanism
     */
    @Override
    public String addNode(NodeDescriptor dsc, Vector nodeServices, boolean propagated) throws IMTPException, ServiceException, JADESecurityException {
        return "Bullshit";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeNode(NodeDescriptor dsc, boolean propagated) throws IMTPException, ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addSlice(ServiceDescriptor service, NodeDescriptor dsc, boolean propagated) throws IMTPException,
                                                                                                   ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeSlice(String serviceKey, String sliceKey, boolean propagated) throws IMTPException,
                                                                                           ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addReplica(String newAddr, boolean propagated) throws IMTPException, ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeReplica(String address, boolean propagated) throws IMTPException, ServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Slice findSlice(String serviceKey, String sliceKey) throws IMTPException, ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Vector findAllSlices(String serviceKey) throws IMTPException, ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void adopt(Node n, Node[] children) throws IMTPException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void ping() throws IMTPException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
