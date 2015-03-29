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
package org.pmedv.core.provider;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.beans.ApplicationMenu;
import org.pmedv.core.beans.ApplicationMenuItem;
import org.pmedv.core.beans.ApplicationMenubar;
import org.pmedv.core.beans.ApplicationPerspective;
import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.beans.RecentFileList;
import org.pmedv.core.commands.Command;
import org.pmedv.core.commands.OpenPerspectiveCommand;
import org.pmedv.core.commands.OpenRecentFileCommand;
import org.pmedv.core.components.CmdJCheckBoxMenuItem;
import org.pmedv.core.components.CmdJMenuItem;
import org.pmedv.core.components.JMenuWithId;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.util.Colors;
import org.springframework.context.ApplicationContext;


/**
 * The <code>ApplicationMenuBarProvider</code> is responsible for the <code>ApplicationMenuBar</code>.
 * It reads the menus and the according items from the file <b>resources/menus.xml</b> and parses them.
 * 
 * @author pueski
 *
 */
public class ApplicationMenuBarProviderImpl implements ApplicationMenuBarProvider {

	private static final Log log = LogFactory.getLog(ApplicationMenuBarProviderImpl.class);

	private final ApplicationContext ctx = AppContext.getApplicationContext();
	
	private JMenuBar menubar;
	private HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
	
	private ArrayList<JMenuWithId> helpMenus = new ArrayList<JMenuWithId>();

	private final String keys[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	/**
	 * @return the menubar
	 */
	public JMenuBar getMenubar() {
		return menubar;
	}

	public ApplicationMenuBarProviderImpl() {
		
		populateKeyTable();
		
		menubar = new JMenuBar();	
		menubar.setBackground(Colors.LIGHT_GRAY);
		
		try {
			JAXBContext c = JAXBContext.newInstance(ApplicationMenubar.class);
			
			Unmarshaller u = c.createUnmarshaller();
			
			// ApplicationMenubar appMenuBar  = (ApplicationMenubar)u.unmarshal(new ClassPathResource("menus.xml").getInputStream());
			
			ApplicationMenubar appMenuBar  = (ApplicationMenubar)u.unmarshal(getClass().getClassLoader().getResourceAsStream("menus.xml"));
			
			for (ApplicationMenu currentMenu : appMenuBar.getMenus()) {
				
				JMenuWithId menu = new JMenuWithId(currentMenu.getName());
				
				menu.setMnemonic(currentMenu.getMnemonic().charAt(0));
				
				for (ApplicationMenuItem currentItem : currentMenu.getItems()) {
					
					try {

						if (currentItem.getActionClass() != null) {

							log.info("Mapping action class : "+currentItem.getActionClass());
							
							Class<?> clazz = Class.forName(currentItem.getActionClass());
							
							try {
								
								Command command = (Command)clazz.newInstance();
								
								ImageIcon icon = null;
								String mnemonic = null;
								String toolTipText = null;
								
								if (currentItem.getImageIcon() != null) {
									
									InputStream is = getClass().getClassLoader().getResourceAsStream(currentItem.getImageIcon());												 
									
									if (is == null) {
										is = getClass().getClassLoader().getResourceAsStream("icons/noresource_16x16.png");
									}
									
									icon = new ImageIcon(ImageIO.read(is));

								}
								if (currentItem.getMnemonic() != null) {
									mnemonic = currentItem.getMnemonic();
								}
								if (currentItem.getToolTipText() != null) {
									toolTipText = currentItem.getToolTipText();
								}
								
								if (currentItem.getType() != null)
									System.out.println("Type "+currentItem.getType());
								
								if (currentItem.getType() != null && currentItem.getType().equals("ApplicationMenuItemType.CHECKBOX")) {
									
									CmdJCheckBoxMenuItem cmdItem = new CmdJCheckBoxMenuItem(currentItem.getName(),icon,command,mnemonic,toolTipText,null);
									
									if (mnemonic != null && currentItem.getModifier() != null) {
										
										if (currentItem.getModifier().equalsIgnoreCase("ctrl")) {
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.CTRL_MASK, false));										
										}
										else if (currentItem.getModifier().equalsIgnoreCase("alt")){
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.ALT_MASK, false));										
										}
										else if (currentItem.getModifier().equalsIgnoreCase("shift")){
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.SHIFT_MASK, false));										
										}

									}
									
									menu.add(cmdItem);		
									
									
								}
								else {
									
									CmdJMenuItem cmdItem = new CmdJMenuItem(currentItem.getName(),icon,command,mnemonic,toolTipText,null);
									
									if (mnemonic != null && currentItem.getModifier() != null) {
										
										if (currentItem.getModifier().equalsIgnoreCase("ctrl")) {
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.CTRL_MASK, false));										
										}
										else if (currentItem.getModifier().equalsIgnoreCase("alt")){
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.ALT_MASK, false));										
										}
										else if (currentItem.getModifier().equalsIgnoreCase("shift")){
											cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.SHIFT_MASK, false));										
										}

									}
									
									menu.add(cmdItem);		
									
								}
								
							} 
							catch (InstantiationException e) {
								log.info("could not instanciate menuitem, skipping.");
							} 
							catch (IllegalAccessException e) {
								log.info("could not access menuitem, skipping.");								
							}
							
						}
						
					} 
					catch (ClassNotFoundException e) {
						log.info("could not find action class "+currentItem.getActionClass());
					} 
					
				}
				
				if (currentMenu.getType() != null && currentMenu.getType().equalsIgnoreCase("file")) {
					
					JMenu openRecentMenu = new JMenu("Open recent...");
				
					RecentFileList fileList = null;
					
					try {
						
						String inputDir = System.getProperty("user.home") + "/."+AppContext.getName()+"/";
						String inputFileName = "recentFiles.xml";			
						File inputFile = new File(inputDir+inputFileName);
						
						if (inputFile.exists()) {
							Unmarshaller u1 = JAXBContext.newInstance(RecentFileList.class).createUnmarshaller(); 							
							fileList = (RecentFileList)u1.unmarshal(inputFile);							
						}
						
						if (fileList == null)
							fileList = new RecentFileList();
						
						for (String recentFile : fileList.getRecentFiles()) {
							CmdJMenuItem item = new CmdJMenuItem(recentFile,null,null);
							
							// TODO : associate generic open file command
							
							File file = new File(recentFile);
							
							item.setCommand(new OpenRecentFileCommand(file));
							openRecentMenu.add(item);
						}
						
					} 
					catch (JAXBException e) {
						e.printStackTrace();
					} 

					menu.add(openRecentMenu);
				}
				
				menu.setId("common");
				
				if (currentMenu.getType() != null && !currentMenu.getType().equalsIgnoreCase("help"))
					menubar.add(menu);
				else
					helpMenus.add(menu);
				
				menubar.setFont(new Font("SansSerif", Font.PLAIN, 12));
				
			}
			
			ApplicationPerspectiveProvider perspectiveProvider = (ApplicationPerspectiveProvider)ctx.getBean(BeanDirectory.PROVIDER_PERSPECTIVE);
			ArrayList<ApplicationPerspective> perspectives = perspectiveProvider.getPerspectives();
			
			JMenuWithId perspectivesMenu = new JMenuWithId("Perspectives");
			perspectivesMenu.setId("common");
			perspectivesMenu.setMnemonic('P');
			
			for (ApplicationPerspective perspective : perspectives) {
 
				ImageIcon icon = null;
				String mnemonic = null;
				String toolTipText = null;
				
				if (perspective.getPerspectiveIcon() != null) {
					
					InputStream is = getClass().getClassLoader().getResourceAsStream(perspective.getPerspectiveIcon());												 
					
					if (is != null) {
						icon = new ImageIcon(ImageIO.read(is));	
					}
					else {
						
						is = getClass().getClassLoader().getResourceAsStream("icons/noresource_16x16.png");

						if (is != null)
							icon = new ImageIcon(ImageIO.read(is));
						
					}
					
				}
				if (perspective.getMnemonic() != null) {
					mnemonic = perspective.getMnemonic();
				}
				if (perspective.getToolTipText() != null) {
					toolTipText = perspective.getToolTipText();
				}
				
				log.info("mapping perspective class "+perspective.getPerspectiveClass());
				OpenPerspectiveCommand command = new OpenPerspectiveCommand(perspective.getId());	
				
				CmdJMenuItem item = new CmdJMenuItem(perspective.getName(),icon,command,mnemonic,toolTipText,null);
				
				if (mnemonic != null && perspective.getModifier() != null) {
					
					if (perspective.getModifier().equalsIgnoreCase("ctrl")) {
						item.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.CTRL_MASK, false));										
					}
					else if (perspective.getModifier().equalsIgnoreCase("alt")){
						item.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.ALT_MASK, false));										
					}
					else if (perspective.getModifier().equalsIgnoreCase("shift")){
						item.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(mnemonic),InputEvent.SHIFT_MASK, false));										
					}

				}
				
				perspectivesMenu.add(item);
				
				for (ApplicationMenu pMenu : perspective.getMenubarContributions()) {
					
					JMenuWithId menu = new JMenuWithId(pMenu.getName());
					
					if (pMenu.getMnemonic() != null && pMenu.getMnemonic().length() > 0)					
						menu.setMnemonic(pMenu.getMnemonic().charAt(0));
					
					for (ApplicationMenuItem currentItem : pMenu.getItems()) {
						
						try {

							if (currentItem.getActionClass() != null) {

								log.info("Mapping action class : "+currentItem.getActionClass());
								
								Class<?> clazz = Class.forName(currentItem.getActionClass());
								
								try {
									
									Command pCommand = (Command)clazz.newInstance();
									
									ImageIcon pIcon = null;
									String pMnemonic = null;
									String pToolTipText = null;
									
									if (currentItem.getImageIcon() != null) {
										
										InputStream is = getClass().getClassLoader().getResourceAsStream(currentItem.getImageIcon());												 
										pIcon = new ImageIcon(ImageIO.read(is));

									}
									if (currentItem.getMnemonic() != null) {
										pMnemonic = currentItem.getMnemonic();
									}
									if (currentItem.getToolTipText() != null) {
										pToolTipText = currentItem.getToolTipText();
									}
									
									System.out.println(currentItem.getType());
									
									if (currentItem.getType() != null && currentItem.getType().equals("ApplicationMenuItemType.CHECKBOX")) {

										log.info("Creating menu checkbox for class "+currentItem.getActionClass());
										
										CmdJCheckBoxMenuItem cmdItem = new CmdJCheckBoxMenuItem(currentItem.getName(),pIcon,pCommand,pMnemonic,pToolTipText,null);
										
										if (pMnemonic != null && currentItem.getModifier() != null) {
											
											if (currentItem.getModifier().equalsIgnoreCase("ctrl")) {
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.CTRL_MASK, false));										
											}
											else if (currentItem.getModifier().equalsIgnoreCase("alt")){
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.ALT_MASK, false));										
											}
											else if (currentItem.getModifier().equalsIgnoreCase("shift")){
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.SHIFT_MASK, false));										
											}

										}
										
										menu.add(cmdItem);
										cmdItem.setSelected(true);
										
										
									}
									else {

										log.info("Creating menu entry for class "+currentItem.getActionClass());
										
										CmdJMenuItem cmdItem = new CmdJMenuItem(currentItem.getName(),pIcon,pCommand,pMnemonic,pToolTipText,null);
										
										if (pMnemonic != null && currentItem.getModifier() != null) {
											
											if (currentItem.getModifier().equalsIgnoreCase("ctrl")) {
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.CTRL_MASK, false));										
											}
											else if (currentItem.getModifier().equalsIgnoreCase("alt")){
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.ALT_MASK, false));										
											}
											else if (currentItem.getModifier().equalsIgnoreCase("shift")){
												cmdItem.setAccelerator(KeyStroke.getKeyStroke(keyMap.get(pMnemonic),InputEvent.SHIFT_MASK, false));										
											}

										}
										
										menu.add(cmdItem);		
										
									}
									
								} 
								catch (InstantiationException e) {
									log.info("could not instanciate menuitem, skipping.");
								} 
								catch (IllegalAccessException e) {
									log.info("could not access menuitem, skipping.");								
								}
								
							}
							
						} 
						catch (ClassNotFoundException e) {
							log.info("could not find action class "+currentItem.getActionClass());
						} 
						
					}
					
					menu.setId(perspective.getId());
					menu.setVisible(false);
					
					menubar.add(menu);

				}
				
				
			}
			menubar.add(perspectivesMenu);
			
			for (JMenuWithId helpmenu : helpMenus) {
				menubar.add(helpmenu);
			}
			
			
			menubar.setFont(new Font("SansSerif", Font.PLAIN, 12));
			
		} 
		catch (JAXBException e) {
			log.info("could not deserialize menus.");
			throw new RuntimeException("could not deserialize menus.");							
		} 
		catch (IOException e) {
			log.info("could not load menus.");
			throw new RuntimeException("could not load menus.");							
		}
		
	}

	/**
	 * Populates the keyMap <code>HashMap</code> with key value pairs
	 * in form of <mnemonic,keyCode> in order to set the right accelerators for any mnemonic 
	 */
	
	private void populateKeyTable() {
		
		for (int i = 0x41;i < 0x5B;i++ ) {
			keyMap.put(keys[i-0x41], i);
		}
		
	}
	
	
}
