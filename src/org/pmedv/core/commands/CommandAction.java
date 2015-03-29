package org.pmedv.core.commands;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

abstract public class CommandAction extends AbstractAction implements Command{
	
	private static final long serialVersionUID = -8291805215011268388L;
	
	public CommandAction(){
		super();
	}
	
	public CommandAction(String name){
		super(name);
	}
	
	public CommandAction(String name, Icon icon){
		super(name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		execute();
	}

}
