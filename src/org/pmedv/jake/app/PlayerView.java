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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

import org.pmedv.core.components.AlternatingLineTable;
import org.pmedv.core.components.CmdJButton;
import org.pmedv.core.components.FilterPanel;
import org.pmedv.core.components.JMarqueeTextField;
import org.pmedv.core.context.AppContext;
import org.pmedv.core.services.ResourceService;
import org.pmedv.core.util.Colors;
import org.springframework.context.ApplicationContext;

public class PlayerView extends JPanel {
	
	private static final long serialVersionUID = 8332804071740959625L;
	private static final ApplicationContext ctx = AppContext.getApplicationContext(); 
	private static final ResourceService resources = (ResourceService)ctx.getBean("resourceService");
	
	CmdJButton prevButton;
	CmdJButton nextButton;
	CmdJButton startButton;
	CmdJButton stopButton;
	
	private JCheckBox randomBox = new JCheckBox();
	
	private JMarqueeTextField titleField;
	private JTextField timeField;
	
	private AlternatingLineTable fileTable;
	private FilterPanel filterPanel;
	
	public PlayerView() {
		super(new BorderLayout());
		fileTable = new AlternatingLineTable();
		fileTable.setColumnControlVisible(true);
		fileTable.setTableHeader(null);
		filterPanel = new FilterPanel();
		filterPanel.setBackground(Colors.SOFT_BLUE_ICE);
		filterPanel.getFilterTextField().setBackground(Colors.LIGHT_GRAY);
		JScrollPane scrollPane = new JScrollPane(fileTable);		
		add(scrollPane, BorderLayout.CENTER);
		add(filterPanel, BorderLayout.SOUTH);
		add(createTopPanel(), BorderLayout.NORTH);
	}

	/**
	 * @return the fileTable
	 */
	public AlternatingLineTable getFileTable() {
	
		return fileTable;
	}


	
	/**
	 * @param fileTable the fileTable to set
	 */
	public void setFileTable(AlternatingLineTable fileTable) {
	
		this.fileTable = fileTable;
	}

	
	/**
	 * @return the filterPanel
	 */
	public FilterPanel getFilterPanel() {
	
		return filterPanel;
	}

	private JPanel createTopPanel() {
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setBackground(Colors.LIGHT_GRAY);
		
		ImageIcon playIcon       = resources.getIcon("icon.play");
		ImageIcon stopIcon       = resources.getIcon("icon.stop");
		ImageIcon nextIcon       = resources.getIcon("icon.next");
		ImageIcon prevIcon       = resources.getIcon("icon.last");
		
		prevButton  = new CmdJButton(prevIcon, "Plays the previous song", null);
		nextButton  = new CmdJButton(nextIcon, "Plays the next song", null);
		startButton = new CmdJButton(playIcon, "Starts playing",   null);
		stopButton  = new CmdJButton(stopIcon, "Stops playing",  null);

		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		buttonPanel.add(prevButton);
		buttonPanel.add(nextButton);
		
		buttonPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), BorderFactory
				.createLineBorder(Color.LIGHT_GRAY)));

		timeField = new JTextField(5);
		timeField.setEditable(false);
		timeField.setBackground(Colors.SOFT_BLUE_ICE);
		titleField = new JMarqueeTextField(30);
		titleField.setEditable(false);
		titleField.setBackground(Colors.SOFT_BLUE_ICE);
		
		buttonPanel.add(timeField);
		buttonPanel.add(titleField);
		
		randomBox = new JCheckBox("R");
		
		buttonPanel.add(randomBox);
		
		return buttonPanel;
		
	}

	
	/**
	 * @return the prevButton
	 */
	public CmdJButton getPrevButton() {
	
		return prevButton;
	}

	
	/**
	 * @return the nextButton
	 */
	public CmdJButton getNextButton() {
	
		return nextButton;
	}

	
	/**
	 * @return the startButton
	 */
	public CmdJButton getStartButton() {
	
		return startButton;
	}

	
	/**
	 * @return the stopButton
	 */
	public CmdJButton getStopButton() {
	
		return stopButton;
	}

	
	/**
	 * @return the titleField
	 */
	public JMarqueeTextField getTitleField() {	
		return titleField;
	}

	
	/**
	 * @return the timeField
	 */
	public JTextField getTimeField() {
	
		return timeField;
	}

	
	/**
	 * @return the randomBox
	 */
	public JCheckBox getRandomBox() {
	
		return randomBox;
	}
	
	
}
