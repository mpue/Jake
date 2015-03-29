package org.jdesktop.application;

import org.jdesktop.application.AbstractBean;

import javax.swing.*;
import java.awt.*;

public class TopLevelFactory<R extends RootPaneContainer> extends AbstractBean {
    private final Class<R> topLevelClass;

    public TopLevelFactory(Class<R> topLevelClass) {
        this.topLevelClass = topLevelClass;
    }

    protected R createTopLevel() {
        R topLevel;
        try {
            topLevel = topLevelClass.newInstance();
        } catch (Exception e) {
            System.err.println("Can't instantiate topLevel instance");
            throw new Error(e);
        }
        configureRootPane(topLevel.getRootPane());
        configureTopLevel(topLevel);
        return topLevel;
    }

    protected Component createMainComponent() {
        return null;
    }
    
    protected JMenuBar createJMenuBar() {
        return null;
    }
    
    protected JToolBar createJToolBar() {
        return null;
    }
    
    protected Component createStatusBar() {
        return null;
    }
    
    protected void configureRootPane(JRootPane rootPane) {
        JMenuBar menuBar = createJMenuBar();
        if (menuBar != null) {
            rootPane.setJMenuBar(menuBar);
        }

        JToolBar toolBar = createJToolBar();
        if (toolBar != null) {
            rootPane.getContentPane().add(toolBar, BorderLayout.PAGE_START);
        }

        Component mainComponent = createMainComponent();
        if (mainComponent != null) {
            rootPane.getContentPane().add(mainComponent);
        }

        Component statusBar = createStatusBar();
        if (statusBar != null) {
            rootPane.getContentPane().add(statusBar, BorderLayout.SOUTH);
        }
    }
    
    protected void configureTopLevel(R topLevel) {
    }
}
