package eugene.utils;

import jade.wrapper.PlatformController.Listener;
import jade.wrapper.PlatformEvent;

/**
 * Adapter for {@link Listener}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public abstract class ListenerAdapter implements Listener {

    @Override
    public void bornAgent(PlatformEvent anEvent) {
    }

    @Override
    public void deadAgent(PlatformEvent anEvent) {
    }

    @Override
    public void startedPlatform(PlatformEvent anEvent) {
    }

    @Override
    public void suspendedPlatform(PlatformEvent anEvent) {
    }

    @Override
    public void resumedPlatform(PlatformEvent anEvent) {
    }

    @Override
    public void killedPlatform(PlatformEvent anEvent) {
    }
}
