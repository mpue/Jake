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
package org.pmedv.jake.commands;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

import org.pmedv.core.beans.BeanDirectory;
import org.pmedv.core.commands.AbstractOpenEditorCommand;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.gui.ApplicationWindow;
import org.pmedv.jake.JakeUtil;
import org.pmedv.jake.app.PlayerController;
import org.pmedv.jake.app.PlayerView;
import org.pmedv.jake.models.FileTableModel;
import org.springframework.context.ApplicationContext;

public class AddFilesCommand extends AbstractOpenEditorCommand implements Command {

	@Override
	public void execute() {

		final ApplicationContext ctx = AppContext.getApplicationContext();
		final ApplicationWindow win = (ApplicationWindow) ctx.getBean(BeanDirectory.WINDOW_APPLICATION);

		/**
		 * Get last selected folder to simplify file browsing
		 */

		if (AppContext.getLastSelectedFolder() == null)
			AppContext.setLastSelectedFolder(System.getProperty("user.home"));

		JFileChooser fc = new JFileChooser(AppContext.getLastSelectedFolder());
		fc.setDialogTitle("Add files");
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(new MP3Filter());

		int result = fc.showOpenDialog(win);

		if (result == JFileChooser.CANCEL_OPTION)
			return;

		File[] files = fc.getSelectedFiles();
		
		PlayerView view = JakeUtil.getCurrentActivePlayer();

		if (view == null) {

			ArrayList<File> fileList = new ArrayList<File>();
			
			for (int i=0; i < files.length;i++)
				fileList.add(files[i]);
			
			final PlayerController controller = new PlayerController(fileList);
			JakeUtil.updateRecentFiles(fc.getSelectedFile().getAbsolutePath());
			AppContext.setLastSelectedFolder(fc.getSelectedFiles()[0].getParentFile().getAbsolutePath());
			
			View v = new View(fc.getSelectedFile().getAbsolutePath(), null, controller.getPlayerView());

			v.addListener(new DockingWindowAdapter() {

				@Override
				public void windowClosing(DockingWindow arg0) throws OperationAbortedException {

					controller.getPlayer().close();
					controller.getPlayFileCommand().setPlaying(false);
				}

			});

			openEditor(v);			
		}
		
		else {

			if (files.length >= 1) {

				AppContext.setLastSelectedFolder(fc.getSelectedFiles()[0].getParentFile().getAbsolutePath());
				JakeUtil.updateRecentFiles(fc.getSelectedFiles()[0].getParentFile().getAbsolutePath());

				FileTableModel model = (FileTableModel) view.getFileTable().getModel();

				for (int i = 0; i < files.length; i++) {
					model.addObject(files[i]);
				}

			}
			
		}

	}

	private static class MP3Filter extends FileFilter {

		@Override
		public boolean accept(File f) {

			if (f.isDirectory())
				return true;
			if ((f.getName().endsWith(".mp3")) || f.getName().endsWith(".MP3"))
				return true;
			else
				return false;
		}

		@Override
		public String getDescription() {

			return "MP3 files only (*.mp3 *.MP3)";
		}

	}

}
