/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package jade.imtp.memory;

import jade.mtp.TransportAddress;

/**
 * Implements the {@link TransportAddress} used by {@link MemoryIMTPManager}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MemoryTransportAddress implements TransportAddress {

    private final String port;

    private final String file;

    private final String anchor;

    /**
     * Constructs a {@link MemoryTransportAddress} with this <code>port</code>, <code>file</code> and
     * <code>anchor.</code>
     *
     * @param port port to use.
     * @param file file to use.
     * @param anchor anchor to use.
     */
    public MemoryTransportAddress(final String port, String file, String anchor) {
        assert null != port;
        assert !port.isEmpty();

        this.port = port;
        this.file = file;
        this.anchor = anchor;
    }

    /**
     * Gets the protocol.
     *
     * @return the protocol.
     *
     * @see {@link MemoryProfile#PROTOCOL}.
     */
    public String getProto() {
        return MemoryProfile.PROTOCOL;
    }

    /**
     * Gets the host.
     *
     * @return the host.
     *
     * @see {@link MemoryProfile#DEFAULT_HOST_NAME}.
     */
    public String getHost() {
        return MemoryProfile.DEFAULT_HOST_NAME;
    }

    /**
     * Gets the port.
     *
     * @return the port.
     */
    public String getPort() {
        return port;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the file.
     *
     * @return the file.
     */
    public String getFile() {
        return file;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the anchor.
     *
     * @return the anchor.
     */
    public String getAnchor() {
        return anchor;
    }
}
