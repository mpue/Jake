package org.jdesktop.application;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Give the Application a chance to veto an attempt to exit/quit.
 * An {@code ExitListener's} {@code canExit} method should return
 * false if there are pending decisions that the user must make
 * before the app exits.  A typical {@code ExitListener} would
 * prompt the user with a modal dialog.
 * <p/>
 * The {@code eventObject} argument will be the the value passed
 * to {@link #exit(java.util.EventObject) exit()}.  It may be null.
 * <p/>
 * The {@code willExit} method is called after the exit has
 * been confirmed.  An ExitListener that's going to perform
 * some cleanup work should do so in {@code willExit}.
 * <p/>
 * {@code ExitListeners} run on the event dispatching thread.
 *
 * @param event the EventObject that triggered this call or null
 * @see #exit(java.util.EventObject)
 * @see #addExitListener
 * @see #removeExitListener
 */
public interface ExitListener extends EventListener {
    boolean canExit(EventObject event);

    void willExit(EventObject event);
}

