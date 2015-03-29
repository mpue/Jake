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
package org.pmedv.core.perspectives;

import javax.swing.JPanel;

import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.ViewMap;

public abstract class AbstractPerspective extends JPanel {

	private static final long serialVersionUID = 5771846683962011959L;

	public String ID;
	
	protected TabWindow editorArea;
	protected RootWindow rootWindow;
	protected DockingWindowListener dockingListener;
	protected ViewMap viewMap;
	
	
	/**
	 * @return the viewMap
	 */
	public ViewMap getViewMap() {
		return viewMap;
	}

	
	/**
	 * @param viewMap the viewMap to set
	 */
	public void setViewMap(ViewMap viewMap) {
		this.viewMap = viewMap;
	}

	/**
	 * @return the rootWindow
	 */
	public RootWindow getRootWindow() {
		return rootWindow;
	}

	/**
	 * @param rootWindow the rootWindow to set
	 */
	public void setRootWindow(RootWindow rootWindow) {
		this.rootWindow = rootWindow;
	}

	/**
	 * @return the dockingListener
	 */
	public DockingWindowListener getDockingListener() {
		return dockingListener;
	}

	/**
	 * @param dockingListener the dockingListener to set
	 */
	public void setDockingListener(DockingWindowListener dockingListener) {
		this.dockingListener = dockingListener;
	}
	
	/**
	 * @return the editorArea
	 */
	public TabWindow getEditorArea() {
		return editorArea;
	}

	/**
	 * @param editorArea the editorArea to set
	 */
	public void setEditorArea(TabWindow editorArea) {
		this.editorArea = editorArea;
	}

	protected abstract void initializeComponents();
	
	protected AbstractPerspective() {
		initializeComponents();
	}
	
	
}
