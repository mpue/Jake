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
package org.pmedv.jake;

import java.io.File;

import javax.swing.JScrollPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.beans.RecentFileList;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindowAdvisor;
import org.pmedv.jake.app.PlayerView;
import org.springframework.context.ApplicationContext;


public class JakeUtil {

	/**
	 * Updates the {@link RecentFileList} with a new file
	 * 
	 * @param filename the name to append to the list
	 */
	public static void updateRecentFiles(String filename) {

		RecentFileList fileList = null;

		try {

			String inputDir = System.getProperty("user.home") + "/."+AppContext.getName()+ "/";
			String inputFileName = "recentFiles.xml";
			File inputFile = new File(inputDir + inputFileName);

			if (inputFile.exists()) {
				Unmarshaller u = JAXBContext.newInstance(RecentFileList.class).createUnmarshaller();
				fileList = (RecentFileList) u.unmarshal(inputFile);
			}

			if (fileList == null)
				fileList = new RecentFileList();

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}

		if (fileList.getRecentFiles().size() >= 5) {
			fileList.getRecentFiles().remove(0);
		}

		if (!fileList.getRecentFiles().contains(filename))
			fileList.getRecentFiles().add(filename);

		Marshaller m;

		try {
			String outputDir = System.getProperty("user.home") + "/."+AppContext.getName()+ "/";
			String outputFileName = "recentFiles.xml";
			m = JAXBContext.newInstance(RecentFileList.class).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			File output = new File(outputDir + outputFileName);
			m.marshal(fileList, output);
		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static PlayerView getCurrentActivePlayer() {
		
		final ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationWindowAdvisor advisor = (ApplicationWindowAdvisor) ctx.getBean(BeanDirectory.ADVISOR_WINDOW_APP);
		
		View view = (View) advisor.getCurrentEditorArea().getSelectedWindow();
		
		if (view != null) {
			PlayerView player = (PlayerView) view.getComponent();			
			return player;			
		}		
		
		return null;
	}
	
}
