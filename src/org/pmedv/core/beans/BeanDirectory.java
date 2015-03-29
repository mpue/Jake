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
package org.pmedv.core.beans;

/**
 * The bean directory takes care of all registered Spring beans which are listed here.
 * We need this in order to access any bean by its name quickly, since it's very time consuming
 * to lookup a service inside the application context xml file.
 * 
 * Every bean name is mapped to a constant
 * 
 * @author mpue
 *
 */
public interface BeanDirectory {
	
	/*
	 * This is just a copy of the bean definitions in applicationContext.xml in order to track them 
	 * 
	 * 	<bean id="applicationWindow" class="org.pmedv.core.gui.ApplicationWindow"/>	
	 *	<bean id="resourceService" class="org.pmedv.core.services.ResourceServiceImpl"/>
	 *	<bean id="menuBarProvider" class="org.pmedv.core.provider.ApplicationMenuBarProvider"/>
	 *	.
	 *	.
	 *	.
	 *
	 */
	
	public static final String WINDOW_APPLICATION      = "applicationWindow";	
	public static final String TOOLBAR_APPLICATION     = "applicationToolbar";
	public static final String SERVICE_RESOURCE        = "resourceService";
	public static final String SERVICE_GALLERY_LOCAL   = "galleryService";
	public static final String SERVICE_TEMPLATE_LOCAL  = "templateService";
	public static final String SERVICE_NODE_LOCAL      = "nodeService";
	public static final String SERVICE_AUTHENTICATION  = "authenticationService";
	public static final String SERVICE_GALLERY_REMOTE  = "galleryRemoteService";
	public static final String SERVICE_TEMPLATE_REMOTE = "templateRemoteService";
	public static final String SERVICE_NODE_REMOTE 	   = "nodeRemoteService";
	public static final String SERVICE_SERVER_ACCOUNT  = "serverAccountService";
	public static final String PROVIDER_PERSPECTIVE    = "perspectiveProvider";
	public static final String PROVIDER_MENUBAR        = "menuBarProvider";
	public static final String PROVIDER_TOOLBAR        = "toolBarProvider";
	public static final String PROVIDER_WINDOW_CONFIG  = "applicationWindowConfig";
	public static final String PERSPECTIVE_GALLERY     = "galleryPerspective";
	public static final String PERSPECTIVE_TEST        = "testPerspective";
	public static final String PERSPECTIVE_CONTENT     = "contentPerspective";
	public static final String MODEL_GALLERY_TREE      = "galleryTreeModel";
	public static final String MODEL_NODE_TREE         = "nodeTreeModel";
	public static final String TREE_GALLERY			   = "galleryTree"; 
	public static final String TREE_NODES			   = "nodeTree";	
	public static final String ADVISOR_WINDOW_APP      = "applicationWindowAdvisor";
	public static final String ROOT_NODE_GALLERY       = "rootNode";
	public static final String ROOT_NODE_CONTENT	   = "contentRootNode";
	public static final String CONTROLLER_LOGIN		   = "loginController";
	public static final String SPLASH_SCREEN 		   = "splashScreen";
	
}
