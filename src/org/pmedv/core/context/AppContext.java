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
package org.pmedv.core.context;

import org.springframework.context.ApplicationContext;

/**
 * This class provides application-wide access to the Spring ApplicationContext.
 * The ApplicationContext is injected by the class "ApplicationContextProvider".
 *
 * @author Matthias Pueski
 */
public class AppContext {

    private static ApplicationContext ctx;    
    private static String lastSelectedFolder = System.getProperty("user.home");
    private static String name;
    
    
	/**
	 * @return the lastSelectedFolder
	 */
	public static String getLastSelectedFolder() {
		return lastSelectedFolder;
	}

	/**
	 * @param lastSelectedFolder the lastSelectedFolder to set
	 */
	public static void setLastSelectedFolder(String lastSelectedFolder) {
		AppContext.lastSelectedFolder = lastSelectedFolder;
	}

	/**
     * Injected from the class "ApplicationContextProvider" which is automatically
     * loaded during Spring-Initialization.
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext;
    }

    /**
     * Get access to the Spring ApplicationContext from everywhere in your Application.
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

	
	/**
	 * @return the name
	 */
	public static String getName() {	
		return name;
	}

	
	/**
	 * @param name the name to set
	 */
	public static void setName(String name) {	
		AppContext.name = name;
	}
    
    
}
