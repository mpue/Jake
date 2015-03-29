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

import java.io.FileInputStream;

import javax.swing.SwingUtilities;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pmedv.core.commands.Command;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.util.ErrorUtils;
import org.pmedv.jake.app.PlayerController;
import org.springframework.context.ApplicationContext;


public class PlayFileCommand implements Command{

	private static final Log log = LogFactory.getLog(PlayFileCommand.class);
	
	private PlayerController controller;

	private boolean playing;
	
	private volatile Player player;
	
	public PlayFileCommand(PlayerController controller) {
		this.controller = controller;
	}
	
	@Override
	public void execute() {
		
		final ApplicationContext ctx = AppContext.getApplicationContext();

		playing = true;
		
		if (controller.getPlayer() != null)
			controller.getPlayer().close();
			
		if (controller.getSelectedFile() == null)
			return;
		
		try {
			FileInputStream fis = new FileInputStream(controller.getSelectedFile());			
			player = new Player(fis);
			controller.setPlayer(player);
			
			String fileIndex = "("+controller.getCurrentIndex()+"/"+controller.getModel().getFiles().size()+") ";
			
			controller.getPlayerView().getTitleField().setText(fileIndex+controller.getSelectedFile().getName());
			controller.getPlayerView().getTitleField().startAnimation();
			
			Thread play = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						player.play();				
					}
					catch (JavaLayerException e) {	
						controller.nextSong();
					}
				}
				
			});
			
			Thread endSongThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while(player != null && !player.isComplete()) {
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}						
					}
					
					if (isPlaying())					
						controller.nextSong();
					
					log.info("Player complete.");
				}
				
			});
			
			Thread displayTime = new Thread(new Runnable() {

				@Override
				public void run() {
					
					while(player != null && !player.isComplete() && isPlaying()) {
						try {
							Thread.sleep(900);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									
									int playerSeconds = player.getPosition()/1000;
									
									int minutes = (playerSeconds - (playerSeconds % 60)) / 60; 
									int seconds = playerSeconds % 60;
									
									StringBuffer time = new StringBuffer();
									
									time.append(minutes);
									time.append(":");
									
									if (seconds < 10)
										time.append("0");
									
									time.append(seconds);
									
									controller.getPlayerView().getTimeField().setText(time.toString());
								
								}								
							});
							
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					
					log.info("Display ended.");
				}
				
			});
			
			play.start();
			endSongThread.start();
			displayTime.start();

		}
		catch (Exception e) {
			ErrorUtils.showErrorDialog(e);
		}
		
	}

	/**
	 * @return the player
	 */
	public synchronized Player getPlayer() {	
		return player;
	}

	
	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {	
		this.player = player;
	}

	
	/**
	 * @return the playing
	 */
	public boolean isPlaying() {
	
		return playing;
	}

	
	/**
	 * @param playing the playing to set
	 */
	public void setPlaying(boolean playing) {
	
		this.playing = playing;
	}

	
}
