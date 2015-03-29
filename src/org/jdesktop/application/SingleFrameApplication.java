/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.io.IOException;
import java.lang.reflect.Method;


public class SingleFrameApplication extends Application<JFrame> {
    private static final Logger logger = Logger.getLogger(SingleFrameApplication.class.getName());

    public SingleFrameApplication() {
        super(JFrame.class);
    }
    
    /**
     * Configures mainWindow when everything else is done,
     * this method adds listeners to the window and makes it visible
     */
    @Override
    protected void configureTopLevel(JFrame mainFrame) {
        configureWindow(mainFrame, "mainFrame");
        mainFrame.addWindowListener(new MainFrameListener());
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // If this is a JFrame monitor "normal" (not maximized) bounds
        mainFrame.addComponentListener(new FrameBoundsListener());
        mainFrame.setVisible(true);
    }

    /**
     * Configures a window: adds listeners, inject resources  
     */
    public void configureWindow(Window root, Object key) {

        // If this is the mainFrame, then close == exit
//        if (getMainTopLevel() == root) {
//        } else { // close == save session state
//            root.addHierarchyListener(new SecondaryWindowListener());
//        }
        
        root.setName(key.toString());
        
        // Inject resources
        getContext().getResourceMap().injectComponents(root);
        
        // Restore session state
        String filename = sessionFilename(root);
        if (filename != null) {
            try {
                if(!getContext().getSessionStorage().restore(root, filename)) {
                    root.pack();
                    root.setLocationRelativeTo(null);
                }
            }
            catch (Exception e) {
                String msg = String.format("couldn't restore sesssion [%s]", filename);
                logger.log(Level.WARNING, msg, e);
            }
        } 
    }

    private class MainFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            exit(e);
        }
    }

    /* In order to properly restore a maximized JFrame, we need to 
    * record it's normal (not maximized) bounds.  They're recorded
    * under a rootPane client property here, so that they've can be 
    * session-saved by WindowProperty#getSessionState().
    */
    private static class FrameBoundsListener extends ComponentAdapter {
        private void maybeSaveFrameSize(ComponentEvent e) {
            if (e.getComponent() instanceof JFrame) {
                JFrame f = (JFrame) e.getComponent();
                if ((f.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                    String clientPropertyKey = "WindowState.normalBounds";
                    f.getRootPane().putClientProperty(clientPropertyKey, f.getBounds());
                }
            }
        }

        public void componentResized(ComponentEvent e) {
            maybeSaveFrameSize(e);
        }

        /* BUG: on Windows XP, with JDK6, this method is called once when the 
        * frame is a maximized, with x,y=-4 and getExtendedState() == 0.
        */
        public void componentMoved(ComponentEvent e) { /* maybeSaveFrameSize(e); */ }
    }

    /* Although it would have been simpler to listen for changes in
    * the secondary window's visibility per either a
    * PropertyChangeEvent on the "visible" property or a change in
    * visibility per ComponentListener, neither listener is notified
    * if the secondary window is disposed.
    * HierarchyEvent.SHOWING_CHANGED does report the change in all
    * cases, so we use that.
    */
    private class SecondaryWindowListener implements HierarchyListener {
        public void hierarchyChanged(HierarchyEvent e) {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (e.getSource() instanceof Window) {
                    Window secondaryWindow = (Window) e.getSource();
                    if (!secondaryWindow.isShowing()) {
                        saveSession(secondaryWindow);
                    }
                }
            }
        }
    }
    
    protected String sessionFilename(Window window) {
        if (window == null) {
            return null;
        } else {
            String name = window.getName();
            return (name == null) ? null : name + ".session.xml";
        }
    }

    protected void saveSession(Window window) {
        String filename = sessionFilename(window);
        if (filename != null) {
            try {
                getContext().getSessionStorage().save(window, filename);
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "couldn't save sesssion", e);
            }
        }
    }

    private boolean isVisibleWindow(Window w) {
        return w.isVisible() &&
                ((w instanceof JFrame) || (w instanceof JDialog) || (w instanceof JWindow));
    }

    /**
     * Return all of the visible JWindows, JDialogs, and JFrames per
     * Window.getWindows() on Java SE 6, or Frame.getFrames() for earlier
     * Java versions.
     */
    private java.util.List<Window> getVisibleSecondaryWindows() {
        java.util.List<Window> rv = new ArrayList<Window>();
        Method getWindowsM = null;
        try {
            getWindowsM = Window.class.getMethod("getWindows");
        }
        catch (Exception ignore) {
        }
        if (getWindowsM != null) {
            Window[] windows = null;
            try {
                windows = (Window[]) getWindowsM.invoke(null);
            }
            catch (Exception e) {
                throw new Error("HCTB - can't get top level windows list", e);
            }
            if (windows != null) {
                for (Window window : windows) {
                    if (isVisibleWindow(window)) {
                        rv.add(window);
                    }
                }
            }
        } else {
            Frame[] frames = Frame.getFrames();
            if (frames != null) {
                for (Frame frame : frames) {
                    if (isVisibleWindow(frame)) {
                        rv.add(frame);
                    }
                }
            }
        }
        return rv;
    }

    /**
     * Save session state for the component hierarchy rooted by
     * the mainFrame.  SingleFrameApplication subclasses that override
     * shutdown need to remember call {@code super.shutdown()}.
     */
    @Override
    protected void shutdown() {
//        saveSession(getMainFrame());
        for (Window window : getVisibleSecondaryWindows()) {
            saveSession(window);
        }
    }
}
