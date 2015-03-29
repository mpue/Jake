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

import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Random;

import javazoom.jl.player.Player;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.pmedv.core.commands.Command;
import org.pmedv.jake.commands.PlayFileCommand;
import org.pmedv.jake.filter.FileFilter;
import org.pmedv.jake.models.FileTableModel;

public class PlayerController {

	private FileTableModel model;
	private PlayerView playerView;
	private File selectedFile;
	private Player player;
	
	private Random randomValue;
	
	private int currentIndex = 0;

	private PlayFileCommand playFileCommand;

	public PlayerController(List<File> files) {
		
		randomValue = new Random(System.currentTimeMillis());
		
		model = new FileTableModel(files);
		playerView = new PlayerView();
		playerView.getFileTable().setModel(model);

		playerView.getFileTable().getColumnExt(1).setVisible(false);
		playerView.getFileTable().getColumnExt(1).setVisible(false);
		
		playFileCommand = new PlayFileCommand(this);

		playerView.getStartButton().setCommand(playFileCommand);
		selectedFile = model.getFiles().get(0);
		currentIndex = 0;

		playerView.getStopButton().setCommand(new Command() {

			@Override
			public void execute() {

				player.close();
				playFileCommand.setPlaying(false);
			}
		});

		playerView.getNextButton().setCommand(new Command() {

			@Override
			public void execute() {

				nextSong();
			}
		});

		playerView.getPrevButton().setCommand(new Command() {

			@Override
			public void execute() {

				previousSong();
			}
		});

		playerView.getFileTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2) {

					int row = playerView.getFileTable().getSelectedRow();
					row = playerView.getFileTable().convertRowIndexToModel(row);
					selectedFile = model.getFiles().get(row);
					currentIndex = row;
					playFileCommand.execute();
					playerView.getTitleField().startAnimation();
					
				}

			}

		});

		FileFilter filter = new FileFilter(playerView.getFileTable());

		BindingGroup filterGroup = new BindingGroup();
		// bind filter JTextBox's text attribute to EventTableFilter's
		// filterString attribute
		filterGroup.addBinding(Bindings.createAutoBinding(READ, playerView.getFilterPanel().getFilterTextField(), BeanProperty
				.create("text"), filter, BeanProperty.create("filterString")));
		filterGroup.bind();		
	}

	public void nextSong() {

		if (model.isRandom())
			currentIndex = randomValue.nextInt(model.getFiles().size() - 1);
			
		if (currentIndex < model.getFiles().size() - 1) {
			currentIndex++;
		}
		else
			currentIndex = 0;

		selectedFile = model.getFiles().get(currentIndex);
		
		playerView.getTitleField().setText(selectedFile.getName());
		playerView.getTitleField().startAnimation();
		playerView.getFileTable().scrollCellToVisible(currentIndex, 0);
		playerView.getFileTable().getSelectionModel().setSelectionInterval(currentIndex, currentIndex);

		playFileCommand.execute();
	}

	public void previousSong() {

		if (model.isRandom())
			currentIndex = randomValue.nextInt(model.getFiles().size() - 1);
		
		if (currentIndex > 0) {
			currentIndex--;
		}
		else
			currentIndex = model.getFiles().size() - 1;

		selectedFile = model.getFiles().get(currentIndex);
		
		playerView.getTitleField().setText(selectedFile.getName());		
		playerView.getFileTable().scrollCellToVisible(currentIndex, 0);
		playerView.getFileTable().getSelectionModel().setSelectionInterval(currentIndex, currentIndex);

		playFileCommand.execute();
	}

	/**
	 * @return the currentIndex
	 */
	public int getCurrentIndex() {

		return currentIndex;
	}

	/**
	 * @param currentIndex the currentIndex to set
	 */
	public void setCurrentIndex(int currentIndex) {

		this.currentIndex = currentIndex;
	}

	/**
	 * @return the playerView
	 */
	public PlayerView getPlayerView() {

		return playerView;
	}

	public File getSelectedFile() {

		return selectedFile;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {

		return player;
	}

	public void setPlayer(Player player) {

		this.player = player;
	}

	/**
	 * @return the playFileCommand
	 */
	public PlayFileCommand getPlayFileCommand() {

		return playFileCommand;
	}

	/**
	 * @param selectedFile the selectedFile to set
	 */
	public void setSelectedFile(File selectedFile) {

		this.selectedFile = selectedFile;
	}

	/**
	 * @return the model
	 */
	public FileTableModel getModel() {	
		return model;
	}

}
