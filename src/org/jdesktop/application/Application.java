/*
* Copyright (C) 2006-2008 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import sun.awt.AppContext;

import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.beans.Beans;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.EventListenerList;


/**
 * The base class for Swing applications.
 * <p/>
 * This class defines a simple lifecyle for Swing applications: {@code
 * initialize}, {@code startup}, {@code ready}, and {@code shutdown}.
 * The {@code Application's} {@code startup} method is responsible for
 * creating the initial GUI and making it visible, and the {@code
 * shutdown} method for hiding the GUI and performing any other
 * cleanup actions before the application exits.  The {@code initialize}
 * method can be used configure system properties that must be set
 * before the GUI is constructed and the {@code ready}
 * method is for applications that want to do a little bit of extra
 * work once the GUI is "ready" to use.  Concrete subclasses must
 * override the {@code startup} method.
 * <p/>
 * Applications are started with the static {@code launch} method.
 * Applications use the {@code ApplicationContext} {@link
 * Application#getContext singleton} to find resources,
 * actions, local storage, and so on.
 * <p/>
 * All {@code Application} subclasses must override {@code startup}
 * and they should call {@link #exit} (which
 * calls {@code shutdown}) to exit.
 * Here's an example of a complete "Hello World" Application:
 * <pre>
 * public class MyApplication extends Application {
 *     JFrame mainFrame = null;
 *     &#064;Override protected void startup() {
 *         mainFrame = new JFrame("Hello World");
 *         mainFrame.add(new JLabel("Hello World"));
 *         mainFrame.addWindowListener(new MainFrameListener());
 *         mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 *         mainFrame.pack();
 *         mainFrame.setVisible(true);
 *     }
 *     &#064;Override protected void shutdown() {
 *         mainFrame.setVisible(false);
 *     }
 *     private class MainFrameListener extends WindowAdapter {
 *         public void windowClosing(WindowEvent e) {
 *            exit();
 *         }
 *     }
 *     public static void main(String[] args) {
 *         Application.launch(MyApplication.class, args);
 *     }
 * }
 * </pre>
 * <p/>
 * The {@code mainFrame's} {@code defaultCloseOperation} is set
 * to {@code DO_NOTHING_ON_CLOSE} because we're handling attempts
 * to close the window by calling
 * {@code ApplicationContext} {@link #exit}.
 * <p/>
 * Simple single frame applications like the example can be defined
 * more easily with the {@link SingleFrameApplication
 * SingleFrameApplication} {@code Application} subclass.
 * <p/>
 * All of the Application's methods are called (must be called) on
 * the EDT.
 * <p/>
 * All but the most trivial applications should define a ResourceBundle
 * in the resources subpackage with the same name as the application class (like {@code
 * resources/MyApplication.properties}).  This ResourceBundle contains
 * resources shared by the entire application and should begin with the
 * following the standard Application resources:
 * <pre>
 * Application.name = A short name, typically just a few words
 * Application.id = Suitable for Application specific identifiers, like file names
 * Application.title = A title suitable for dialogs and frames
 * Application.version = A version string that can be incorporated into messages
 * Application.vendor = A proper name, like Sun Microsystems, Inc.
 * Application.vendorId = suitable for Application-vendor specific identifiers, like file names.
 * Application.homepage = A URL like http://www.javadesktop.org
 * Application.description =  One brief sentence
 * Application.lookAndFeel = either system, default, or a LookAndFeel class name
 * </pre>
 * <p/>
 * The {@code Application.lookAndFeel} resource is used to initialize the
 * {@code UIManager lookAndFeel} as follows:
 * <ul>
 * <li>{@code system} - the system (native) look and feel</li>
 * <li>{@code default} - use the JVM default, typically the cross platform look and feel</li>
 * <li>a LookAndFeel class name - use the specified class
 * </ul>
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see SingleFrameApplication
 * @see ApplicationContext
 * @see UIManager#setLookAndFeel
 */

@ProxyActions({"cut", "copy", "paste", "delete"})

public abstract class Application<R extends RootPaneContainer> 
        extends TopLevelFactory<R> {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private final EventListenerList exitListeners;
    private final ApplicationContext context;
    private R mainTopLevel;

    /**
     * Not to be called directly, see {@link #launch launch}.
     * <p/>
     * Subclasses can provide a no-args construtor
     * to initialize private final state however GUI
     * initialization, and anything else that might refer to
     * public API, should be done in the {@link #startup startup}
     * method.
     */
    protected Application(Class<R> topLevelClass) {
        super(topLevelClass);
        exitListeners = new EventListenerList();
        context = createApplicationContext();
    }

    /**
     * Creates an instance of the specified {@code Application}
     * subclass, sets the {@code ApplicationContext} {@code
     * application} property, and then calls the new {@code
     * Application's} {@code startup} method.  The {@code launch} method is
     * typically called from the Application's {@code main}:
     * <pre>
     *     public static void main(String[] args) {
     *         Application.launch(MyApplication.class, args);
     *     }
     * </pre>
     * The {@code applicationClass} constructor and {@code startup} methods
     * run on the event dispatching thread.
     *
     * @param applicationClass the {@code Application} class to launch
     * @param args             {@code main} method arguments
     * @see #shutdown
     * @see ApplicationContext#getApplication
     */
    public static <T extends Application> void launch(final Class<T> applicationClass, final String[] args) {
        Runnable doCreateAndShowGUI = new Runnable() {
            public void run() {
                try {
                    Application application = create(applicationClass);
                    application.launch(args);
                }
                catch (Exception e) {
                    String msg = String.format("Application %s failed to launch", applicationClass);
                    logger.log(Level.SEVERE, msg, e);
                    throw (new Error(msg, e));
                }
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
    
    public void launch(String[] args) {
        AppContext.getAppContext().put(ApplicationContext.class, context);
        initialize(args);
        mainTopLevel = createTopLevel();
        startup();
    }

    public R getMainTopLevel() {
        return mainTopLevel;
    }

    /* Initializes the ApplicationContext applicationClass and application
     * properties.  
     * 
     * Note that, as of Java SE 5, referring to a class literal
     * doesn't force the class to be loaded.  More info:
     * http://java.sun.com/javase/technologies/compatibility.jsp#literal
     * It's important to perform these initializations early, so that
     * Application static blocks/initializers happen afterwards.
     */
    static <T extends Application> T create(Class<T> applicationClass) throws Exception {

        /* Construct the Application object.  The following
         * complications, relative to just calling
         * applicationClass.newInstance(), allow a privileged app to
         * have a private static inner Application subclass.
         */
        Constructor<T> ctor = applicationClass.getDeclaredConstructor();
        if (!ctor.isAccessible()) {
            try {
                ctor.setAccessible(true);
            }
            catch (SecurityException ignore) {
                // ctor.newInstance() will throw an IllegalAccessException
            }
        }
        T application = ctor.newInstance();
        return application;
    }
    
    protected ApplicationContext createApplicationContext() {

        /* Initialize the ApplicationContext application properties
         */
        ApplicationContext ctx = new ApplicationContext();
        ctx.setApplicationClass(getClass());
        ctx.setApplication(this);

        /* Load the application resource map, notably the 
          * Application.* properties.
          */
        ResourceMap appResourceMap = ctx.getResourceMap();

        appResourceMap.putResource("platform", platform());
        return ctx;
    }

    /* Defines the default value for the platform resource, 
     * either "osx" or "default".
     */
    private static String platform() {
        String platform = "default";
        try {
            String osName = System.getProperty("os.name");
            if ((osName != null) && osName.toLowerCase().startsWith("mac os x")) {
                platform = "osx";
            }
        }
        catch (SecurityException ignore) {
        }
        return platform;
    }

    /**
     * Responsible for initializations that must occur before the
     * GUI is constructed by {@code startup}.
     * <p/>
     * This method is called by the static {@code launch} method,
     * before {@code startup} is called. Subclasses that want
     * to do any initialization work before {@code startup} must
     * override it.  The {@code initialize} method
     * runs on the event dispatching thread.
     * <p/>
     * By default initialize() does nothing.
     *
     * @param args the main method's arguments.
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void initialize(String[] args) {
        if (!Beans.isDesignTime()) {
            /* Initialize the UIManager lookAndFeel property with the
             * Application.lookAndFeel resource.  If the the resource
             * isn't defined we default to "system".
             */
            String key = "Application.lookAndFeel";
            String lnfResource = getContext().getResourceMap().getString(key);
            String lnf = (lnfResource == null) ? "system" : lnfResource;
            try {
                if (lnf.equalsIgnoreCase("system")) {
                    String name = UIManager.getSystemLookAndFeelClassName();
                    UIManager.setLookAndFeel(name);
                } else if (!lnf.equalsIgnoreCase("default")) {
                    UIManager.setLookAndFeel(lnf);
                }
            }
            catch (Exception e) {
                String s = "Couldn't set LookandFeel " + key + " = \"" + lnfResource + "\"";
                logger.log(Level.WARNING, s, e);
            }
        }
    }

    /**
     * Responsible for starting the application; for creating and showing
     * the initial GUI.
     * <p/>
     * This method is called by the static {@code launch} method,
     * subclasses must override it.  It runs on the event dispatching
     * thread.
     *
     * @see #launch
     * @see #initialize
     * @see #shutdown
     */
    protected void startup() {
    }

    /**
     * Called after the startup() method has returned and there
     * are no more events on the
     * {@link Toolkit#getSystemEventQueue system event queue}.
     * When this method is called, the application's GUI is ready
     * to use.
     * <p/>
     * It's usually important for an application to start up as
     * quickly as possible.  Applications can override this method
     * to do some additional start up work, after the GUI is up
     * and ready to use.
     *
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void ready() {
    }

    /**
     * Called when the application {@link #exit exits}.
     * Subclasses may override this method to do any cleanup
     * tasks that are neccessary before exiting.  Obviously, you'll want to try
     * and do as little as possible at this point.  This method runs
     * on the event dispatching thread.
     *
     * @see #startup
     * @see #ready
     * @see #exit
     * @see #addExitListener
     */
    protected void shutdown() {
        // TBD should call TaskService#shutdownNow() on each TaskService
    }

    /**
     * Gracefully shutdown the application, calls {@code exit(null)}
     * This version of exit() is convenient if the decision to exit the
     * application wasn't triggered by an event.
     *
     * @see #exit(EventObject)
     */
    public final void exit() {
        exit(null);
    }

    /**
     * Gracefully shutdown the application.
     * <p/>
     * If none of the {@code ExitListener.canExit()} methods return false,
     * calls the {@code ExitListener.willExit()} methods, then
     * {@code shutdown()}, and then exits the Application with
     * {@link #end end}.  Exceptions thrown while running willExit() or shutdown()
     * are logged but otherwise ignored.
     * <p/>
     * If the caller is responding to an GUI event, it's helpful to pass the
     * event along so that ExitListeners' canExit methods that want to popup
     * a dialog know on which screen to show the dialog.  For example:
     * <pre>
     * class ConfirmExit implements Application.ExitListener {
     *     public boolean canExit(EventObject e) {
     *         Object source = (e != null) ? e.getSource() : null;
     *         Component owner = (source instanceof Component) ? (Component)source : null;
     *         int option = JOptionPane.showConfirmDialog(owner, "Really Exit?");
     *         return option == JOptionPane.YES_OPTION;
     *     }
     *     public void willExit(EventObejct e) {}
     * }
     * myApplication.addExitListener(new ConfirmExit());
     * </pre>
     * The {@code eventObject} argument may be null, e.g. if the exit
     * call was triggered by non-GUI code, and {@code canExit}, {@code
     * willExit} methods must guard against the possibility that the
     * {@code eventObject} argument's {@code source} is not a {@code
     * Component}.
     *
     * @param event the EventObject that triggered this call or null
     * @see #addExitListener
     * @see #removeExitListener
     * @see #shutdown
     * @see #end
     */
    public void exit(EventObject event) {
        ExitListener[] listeners = exitListeners.getListeners(ExitListener.class);
        for (ExitListener listener : listeners) {
            if (!listener.canExit(event)) {
                return;
            }
        }
        try {
            for (ExitListener listener : listeners) {
                try {
                    listener.willExit(event);
                }
                catch (Exception e) {
                    logger.log(Level.WARNING, "ExitListener.willExit() failed", e);
                }
            }
            shutdown();
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "unexpected error in Application.shutdown()", e);
        }
        finally {
            end();
        }
    }

    /**
     * Called by {@link #exit exit} to terminate the application.  Calls
     * {@code Runtime.getRuntime().exit(0)}, which halts the JVM.
     *
     * @see #exit
     */
    protected void end() {
        AppContext.getAppContext().remove(ApplicationContext.class);
        Runtime.getRuntime().exit(0);
    }

    /**
     * Add an {@code ExitListener} to the list.
     *
     * @param listener the {@code ExitListener}
     * @see #removeExitListener
     * @see #getExitListeners
     */
    public void addExitListener(ExitListener listener) {
        exitListeners.add(ExitListener.class, listener);
    }

    /**
     * Remove an {@code ExitListener} from the list.
     *
     * @param listener the {@code ExitListener}
     * @see #addExitListener
     * @see #getExitListeners
     */
    public void removeExitListener(ExitListener listener) {
        exitListeners.remove(ExitListener.class, listener);
    }

    /**
     * All of the {@code ExitListeners} added so far.
     *
     * @return all of the {@code ExitListeners} added so far.
     */
    public ExitListener[] getExitListeners() {
        return exitListeners.getListeners(ExitListener.class);
    }

    /**
     * The default {@code Action} for quitting an application,
     * {@code quit} just exits the application by calling {@code exit(e)}.
     *
     * @param e the triggering event
     * @see #exit(EventObject)
     */
    @Action
    public void quit(ActionEvent e) {
        exit(e);
    }

    /**
     * The ApplicationContext singleton for this Application.
     *
     * @return the Application's ApplicationContext singleton
     */
    public static ApplicationContext getContext() {
        return (ApplicationContext) AppContext.getAppContext().get(ApplicationContext.class);
    }
}
