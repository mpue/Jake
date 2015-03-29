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

import org.pmedv.core.commands.Command;
import org.pmedv.jake.JakeUtil;
import org.pmedv.jake.app.PlayerView;
import org.pmedv.jake.models.FileTableModel;


public class ToggleRandomCommand implements Command {

	@Override
	public void execute() {
	
		PlayerView view = JakeUtil.getCurrentActivePlayer();
		
		FileTableModel model = (FileTableModel)view.getFileTable().getModel();
		model.setRandom(!model.isRandom());
		view.getRandomBox().setSelected(model.isRandom());
		
	}

}
