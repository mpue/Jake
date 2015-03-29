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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import net.infonode.docking.TabWindow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.Constants;
import org.pmedv.core.beans.ApplicationWindowConfiguration;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.perspectives.AbstractPerspective;
import org.pmedv.core.provider.ApplicationWindowConfigurationProvider;
import org.pmedv.core.services.ResourceService;
import org.springframework.context.ApplicationContext;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jgoodies.looks.plastic.theme.SkyRed;


public class ApplicationWindowAdvisorImpl implements ApplicationWindowAdvisor {

	private static final Log log = LogFactory.getLog(ApplicationWindowAdvisorImpl.class);
	
	private ApplicationContext ctx;
	private ApplicationWindow win;
	private final ResourceService resources;
	private ApplicationWindowConfigurationProvider windowConfig;
	private TabWindow currentEditorArea;
	private AbstractPerspective currentPerspective;
	private int numberOfDisplays;
	
	public ApplicationWindowAdvisorImpl() {		
		ctx = AppContext.getApplicationContext();
		resources = (ResourceService)ctx.getBean(BeanDirectory.SERVICE_RESOURCE);
		windowConfig = (ApplicationWindowConfigurationProvider)ctx.getBean(BeanDirectory.PROVIDER_WINDOW_CONFIG);		
	}
	
	/**
	 * @return the windowConfig
	 */
	public ApplicationWindowConfigurationProvider getWindowConfig() {
		return windowConfig;
	}

	@Override
	public void windowActivatedHook(WindowEvent e) {
	}

	@Override
	public void windowClosedHook(WindowEvent e) {
	}

	@Override
	public void windowClosingHook(WindowEvent e) {
		
		System.out.println(win.getX()+","+win.getY()+" "+win.getWidth()+","+win.getHeight());
		
		windowConfig.getConfig().setX(win.getX());
		windowConfig.getConfig().setY(win.getY());
		windowConfig.getConfig().setHeight(win.getHeight());
		windowConfig.getConfig().setWidth(win.getWidth());
		windowConfig.storeConfig();
		
		System.exit(0);
	}

	@Override
	public void windowDeactivatedHook(WindowEvent e) {
	}

	@Override
	public void windowDeiconifiedHook(WindowEvent e) {
	}

	@Override
	public void windowIconifiedHook(WindowEvent e) {
	}

	@Override
	public void windowOpenedHook(WindowEvent e) {
	}

	@Override
	public void preWindowCreate() {
		
		log.info("initializing.");

		Plastic3DLookAndFeel.setPlasticTheme(new ExperienceBlue());

		try {			
			UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			com.jgoodies.looks.Options.setPopupDropShadowEnabled(true);
		} 
		catch (Exception e) {
			log.info("failed to set look and feel.");
		}
		
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} 
//		catch (Exception e) {
//			log.info("failed to set look and feel.");
//		}

		log.info("setting look and feel to: "+UIManager.getLookAndFeel());

		// construct app icon
		
		Image iconImage = resources.getIcon("icon.application").getImage();

		MediaTracker mt = new MediaTracker(win);
		mt.addImage(iconImage, 0);

		try {
			mt.waitForAll();
		} 
		catch (InterruptedException e) {
			// Silently ignore
		}

		win.setTitle(windowConfig.getConfig().getTitle()+" Version "+Constants.VERSION);
		win.setIconImage(iconImage);
		win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		win.addWindowListener(win);
				
		ToolTipManager.sharedInstance().setInitialDelay(800);
		ToolTipManager.sharedInstance().setDismissDelay(2000);


	}

	
	@Override
	public void postWindowCreate() {
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle frame = win.getBounds();
		
		ApplicationWindowConfiguration config = windowConfig.getConfig();
		
		if (config.getX() > 0 && config.getY() > 0) { 
			win.setLocation(config.getX() ,config.getY());			
		}
		else {
			win.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
			Rectangle bounds = new Rectangle(0, 0, screen.width ,screen.height  - 35);
			win.setBounds(bounds);
		}
		if (config.getHeight() > 0 && config.getWidth() > 0) { 
			win.setSize(config.getWidth() ,config.getHeight());			
		}
		else {
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			numberOfDisplays = env.getScreenDevices().length;
			win.setMaximizedBounds(env.getMaximumWindowBounds ());
			win.setExtendedState(win.getExtendedState() | Frame.MAXIMIZED_BOTH);			
		}
		if (!config.isStatusbarVisible())
			win.getStatusBar().setVisible(false);
		
	}

	@Override
	public void setWindow(ApplicationWindow win) {
		this.win = win;
		
	}

	@Override
	public TabWindow getCurrentEditorArea() {
		return currentEditorArea;
	}

	@Override
	public void setCurrentEditorArea(TabWindow area) {
		currentEditorArea = area;		
	}

	@Override
	public AbstractPerspective getCurrentPerspective() {
		return currentPerspective;
	}

	@Override
	public void setCurrentPerspective(AbstractPerspective perspective) {
		currentPerspective = perspective;
		
	}

	/**
	 * @return the numberOfDisplays
	 */
	public int getNumberOfDisplays() {
		return numberOfDisplays;
	}

}
