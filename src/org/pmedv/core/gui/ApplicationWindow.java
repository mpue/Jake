/**

	Jake
	Written and maintained by Matthias Pueski 
	
	Copyright (c) 2009 Matthias Pueski
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.pmedv.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXStatusBar.Constraint;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.provider.ApplicationMenuBarProvider;
import org.pmedv.core.provider.ApplicationToolbarProvider;
import org.springframework.context.ApplicationContext;


/**
 * The main application window
 * 
 * @author mpue
 * 
 * 10.12.2006
 *
 */

public class ApplicationWindow extends AbstractApplicationWindow {
	
	private static final long serialVersionUID = 9047805487938155205L;
	
	private JPanel       layoutPane;
	private JLabel       statusLabel;
	private JLabel 		 hostLabel;
	private JProgressBar progressBar;
	private JMenuBar     menuBar;
	private JToolBar	 toolBar;
	private JXStatusBar  statusBar;
	
	@Override
	protected void initializeComponents() {
	
		ApplicationContext ctx = AppContext.getApplicationContext();
		
		layoutPane = new JPanel(new BorderLayout());	
		menuBar = createMenuBar();
		
		this.setJMenuBar(menuBar);
		this.add(layoutPane,java.awt.BorderLayout.CENTER);
		
		toolBar = createToolBar();
		
		JPanel toolbarPanel = new JPanel(new BorderLayout());
		this.add(toolBar,java.awt.BorderLayout.NORTH);
		
		statusBar = new JXStatusBar();

		ImageIcon offlineIcon = resources.getIcon("icon.status.offline"); 

		statusLabel = new JLabel();		
		statusBar.add(statusLabel);
		
		statusLabel = new JLabel("Ready");
		JXStatusBar.Constraint c1 = new Constraint(); 
		c1.setFixedWidth(200);
		statusBar.add(statusLabel, c1);     // Fixed width of 100 with no inserts

		statusLabel.setIcon(offlineIcon);
		
		hostLabel = new JLabel("Not connected.");
		JXStatusBar.Constraint c3 = new Constraint(); 
		c3.setFixedWidth(300);		
		statusBar.add(hostLabel,c3);
		
		JXStatusBar.Constraint c2 = new Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
		progressBar = new JProgressBar();
		statusBar.add(progressBar, c2);		
		
		this.add(statusBar,java.awt.BorderLayout.SOUTH);
		
		layoutPane.setBackground(Color.WHITE);

	}
	
	/**
	 * @return the statusLabel
	 */
	public JLabel getStatusLabel() {
		return statusLabel;
	}

	/**
	 * @param statusLabel the statusLabel to set
	 */
	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}

	/**
	 * @return the application's menu bar
	 */
	private JMenuBar createMenuBar() {		
		ApplicationMenuBarProvider p = (ApplicationMenuBarProvider)ctx.getBean(BeanDirectory.PROVIDER_MENUBAR);		
		return p.getMenubar();
	}
	
	private JToolBar createToolBar() {
		ApplicationToolbarProvider p = (ApplicationToolbarProvider)ctx.getBean(BeanDirectory.PROVIDER_TOOLBAR);
		return p.getToolbar();		
	}

	/**
	 * @return the progressBar
	 */
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @return the layoutPane
	 */
	public JPanel getLayoutPane() {
		return layoutPane;
	}

	/**
	 * @return the toolBar
	 */
	public JToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * @param toolBar the toolBar to set
	 */
	public void setToolBar(JToolBar toolbar) {
		this.toolBar = toolbar;
	}

	/**
	 * @return the menuBar
	 */
	public JMenuBar getAppMenuBar() {
		return menuBar;
	}

	/**
	 * @return the hostLabel
	 */
	public JLabel getHostLabel() {
	
		return hostLabel;
	}

	public JXStatusBar getStatusBar() {
		return statusBar;
	}
	
	
}
