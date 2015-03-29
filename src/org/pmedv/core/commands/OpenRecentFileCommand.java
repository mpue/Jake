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
package org.pmedv.core.commands;

import java.io.File;
import java.util.ArrayList;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

import org.pmedv.core.context.AppContext;
import org.pmedv.core.util.FileUtils;
import org.pmedv.jake.app.PlayerController;


public class OpenRecentFileCommand extends AbstractOpenEditorCommand implements Command {

	private File file;
	
	public OpenRecentFileCommand(File file) {
		this.file = file;
	}
	
	@Override
	public void execute() {
		
		ArrayList<File> files = new ArrayList<File>();
		
		if (file.isDirectory()) {
			FileUtils.findFile(files, file, ".mp3", true, true);
		}
		else
			files.add(file);
			
		final PlayerController controller = new PlayerController(files);
		View view = new View(file.getAbsolutePath(), null, controller.getPlayerView());
		
		if (AppContext.getLastSelectedFolder() == null)
			AppContext.setLastSelectedFolder(System.getProperty("user.home"));
		else
			AppContext.setLastSelectedFolder(file.getAbsolutePath());
		
		view.addListener(new DockingWindowAdapter() {

			@Override
			public void windowClosing(DockingWindow arg0) throws OperationAbortedException {
				if (controller.getPlayer() != null)
					controller.getPlayer().close();
				controller.getPlayFileCommand().setPlaying(false);
			}

		});		
		
		openEditor(view);
		
	};

}
