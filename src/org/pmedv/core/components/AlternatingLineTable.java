package org.pmedv.core.components;

import java.awt.Component;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.pmedv.core.util.Colors;

public class AlternatingLineTable extends JXTable {

	private static final long serialVersionUID = -3221298344485063709L;
	
	public AlternatingLineTable() {
		super();
	}
	
	public AlternatingLineTable(TableModel model) {
		super(model);
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		
		Component c = super.prepareRenderer(renderer, row, column);

		if (row % 2 == 0 && !isCellSelected(row, column)) {
			c.setBackground(Colors.LIGHT_GRAY);
		}
		else {
			if (isCellSelected(row, column)) {
				c.setBackground(Colors.GRAY_BLUE);
			}
			else {
				c.setBackground(Colors.SOFT_BLUE_ICE);
			}
		}
		
		return c;

	}
	
}
