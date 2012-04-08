/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.ontology.Message;

/**
 * Proxies {@link Message}s to <code>applications</code>.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface ProxyApplication extends Application {

    /**
     * Adds an {@link Application} to this proxy.
     *
     * @param application {@link Application} to add.
     *
     * @return <tt>true</tt> if the element was added.
     */
    boolean addApplication(final Application application);

    /**
     * Removes an {@link Application} from this proxy.
     *
     * @param application {@link Application} to remove.
     *
     * @return <tt>true</tt> if this {@link ProxyApplication} contained this <code>application</code>.
     */
    boolean removeApplication(final Application application);
}
