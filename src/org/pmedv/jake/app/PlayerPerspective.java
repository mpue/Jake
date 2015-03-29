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
package org.pmedv.jake.app;

import java.awt.BorderLayout;

import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.TabWindow;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.core.perspectives.AbstractPerspective;
import org.pmedv.core.services.ResourceService;
import org.springframework.context.ApplicationContext;


public class PlayerPerspective extends AbstractPerspective {

	private static final long serialVersionUID = -7124330724281055470L;

	private static final Log log = LogFactory.getLog(PlayerPerspective.class);

	private ApplicationContext ctx;
	private ResourceService resources;
	private ApplicationWindowAdvisor advisor;
	
	public PlayerPerspective() {
		ID = "playerPerspective";
		setName("playerPerspective");
		log.info("Initializing "+getName());
	}	
	
	@Override
	protected void initializeComponents() {
		setLayout(new BorderLayout());

		ctx = AppContext.getApplicationContext();
		resources = (ResourceService)ctx.getBean(BeanDirectory.SERVICE_RESOURCE);
		advisor = (ApplicationWindowAdvisor)ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				ViewMap viewMap = new ViewMap();
				
				rootWindow = DockingUtil.createRootWindow(viewMap, true);

				DockingWindowsTheme theme = new SoftBlueIceDockingTheme();

				rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());
				rootWindow.getWindowProperties().getTabProperties().getHighlightedButtonProperties().getCloseButtonProperties().setVisible(false);
				rootWindow.getWindowProperties().getTabProperties().getNormalButtonProperties().getCloseButtonProperties().setVisible(false);
				
				editorArea = new TabWindow();
				editorArea.getWindowProperties().setCloseEnabled(false);
				editorArea.getWindowProperties().getTabProperties().getNormalButtonProperties().getCloseButtonProperties().setVisible(false);
				editorArea.getWindowProperties().getTabProperties().getHighlightedButtonProperties().getCloseButtonProperties().setVisible(false);

				DockingWindowAdapter dockingAdapter = new DockingWindowAdapter() {

					@Override
					public void windowClosing(DockingWindow window) throws OperationAbortedException {

					}
				};

				editorArea.addListener(dockingAdapter);
				setDockingListener(dockingAdapter);

				rootWindow.setWindow(editorArea);
				add(rootWindow, BorderLayout.CENTER);
				
				advisor.setCurrentEditorArea(editorArea);				
				
			}

		});

		
	}


}
