package org.pmedv.jake.filter;

import java.io.File;
import java.util.regex.Pattern;

import javax.swing.RowFilter;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXTable;
import org.pmedv.jake.models.FileTableModel;

public class FileFilter extends AbstractBean {

	private RowFilter<Object, Object> searchFilter;
	private String filterString;
	private JXTable partTable;

	public FileFilter(JXTable emailTable) {
		this.partTable = emailTable;
	}

	public String getFilterString() {
		return filterString;
	}

	/**
	 * Sets the filter string
	 * 
	 * @param filterString the filterString to set
	 */
	public void setFilterString(String filterString) {
		String oldValue = getFilterString();
		this.filterString = filterString;
		updateSearchFilter();
		firePropertyChange("filterString", oldValue, getFilterString());
	}

	private void updateSearchFilter() {
		if (filterString != null) {
			searchFilter = createSearchFilter(".*" + filterString + ".*");
		}
		updateFilters();
	}

	private void updateFilters() {
		// set the filters to the table
		if (searchFilter != null) {
			partTable.setRowFilter(searchFilter);
		}
	}

	private RowFilter<Object, Object> createSearchFilter(final String filterString) {
		return new RowFilter<Object, Object>() {

			@Override
			public boolean include(Entry<? extends Object, ? extends Object> entry) {

				boolean matches = false;

				FileTableModel model = (FileTableModel) entry.getModel();

				int modelIndex = ((Integer) entry.getIdentifier()).intValue();

				File file = model.getFiles().get(modelIndex);

				String name = file.getName();				
				if (name == null)
					name = "";

				Pattern p = Pattern.compile(filterString, Pattern.CASE_INSENSITIVE);
				matches = (p.matcher(name).matches());

				return matches;
			}
		};
	}

}
