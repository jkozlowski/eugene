/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package jade.imtp.memory;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.leap.ArrayList;

/**
 * Default {@link Profile} for creating containers with {@link MemoryIMTPManager}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MemoryProfile extends ProfileImpl {

    /**
     * Property for an instance of an {@link MemoryIMTPManager}.
     */
    public static final String MEMORY_IMTP_INSTANCE_KEY = "memory-imtp-instance";

    /**
     * Default host name.
     *
     */
    public static final String DEFAULT_HOST_NAME = "localhost";

    /**
     * Protocol name.
     *
     */
    public static final String PROTOCOL = "mem";

    public MemoryProfile() {
        setParameter(Profile.IMTP, MemoryIMTPManager.class.getName());
        setParameter(Profile.NO_MTP, Boolean.TRUE.toString());
        setParameter(Profile.DETECT_MAIN, Boolean.FALSE.toString());
        setParameter(Profile.LOCAL_SERVICE_MANAGER, Boolean.FALSE.toString());
        setParameter(Profile.NO_DISPLAY, Boolean.TRUE.toString());
        setSpecifiers(Profile.MTPS, new ArrayList(0));
        setParameter(Profile.MTPS, null);
    }
}
