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
package org.pmedv.jake.models;

import java.io.File;
import java.util.List;

import org.pmedv.core.model.AbstractBaseTableModel;

/**
 * Table model for a JTable containing objects of type <code>File</code>
 * 
 * @author Matthias Pueski
 *
 */
public class FileTableModel extends AbstractBaseTableModel {

	private final String[] columnNames = { "Name","Size","last modified" };
	
	private boolean random = false;
	
	private List<File> files;

	public FileTableModel(List<File> files) {
		
		this.files = files;
		
		data = new Object[files.size()][3];
		columns = 3;
		rows = files.size();
		
		int index = 0;
		
		for (File o : files) {
			
			data[index][0] = o.getName();
			data[index][1] = o.length();
			data[index][2] = o.lastModified();
			
			index++;
		}
	
	}

	public void addObject(File object) {

		files.add(object);
		
		resizeTable(files.size(), columns);

		rows = files.size(); 
		
		data[files.size()-1][0] = object.getName();
		data[files.size()-1][1] = object.length();
		data[files.size()-1][2] = object.lastModified();
				
		fireTableDataChanged();
		
	}
	
	public void updateRow(int row, File o) {

		data[row][0] = o.getName();
		data[row][1] = o.length();
		data[row][2] = o.lastModified();

		fireTableCellUpdated(row, 0);
		fireTableCellUpdated(row, 1);
		fireTableCellUpdated(row, 2);
		
		fireTableDataChanged();

	}
	
	public void removeObject(File object) {
		
		files.remove(object);
		
		/** 
		 * If a table row has been removed, we simply build up the whole data from scratch.
		 * This is okay, because the template table contains not much data anyway.
		 * 
		 * IMHO this is FASTER than resizing the array;
		 */
		
		data = new Object[files.size()][3];
		rows = files.size();
		
		int index = 0;
		
		for (File o : files) {
			
			data[index][0] = o.getName();
			data[index][1] = o.length();
			data[index][2] = o.lastModified();
			
			fireTableCellUpdated(index, 0);
			fireTableCellUpdated(index, 1);
			fireTableCellUpdated(index, 2);
			
			index++;
		}
		
		fireTableDataChanged();
		
	}
	

	/**
	 * @return the files
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<File> files) {

		this.files = files;
		
		data = new Object[files.size()][3];
		columns = 3;
		rows = files.size();
		
		int index = 0;
		
		for (File o : files) {
			
			data[index][0] = o.getName();
			data[index][1] = o.length();
			data[index][2] = o.lastModified();
			
			index++;
	
		}
		
		fireTableDataChanged();

	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 1: return String.class;
		case 2: return Long.class;
		case 3: return Long.class;
		default : return String.class;
		}
	}

	
	/**
	 * @return the random
	 */
	public boolean isRandom() {
	
		return random;
	}

	
	/**
	 * @param random the random to set
	 */
	public void setRandom(boolean random) {
	
		this.random = random;
	}
	
	
}
