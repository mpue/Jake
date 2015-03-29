/*
 * Created by JFormDesigner on Wed Sep 15 12:07:57 CEST 2010
 */

package org.pmedv.core.components;

import java.awt.event.*;
import java.util.ResourceBundle;

import javax.swing.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;

/**
 * @author Matthias Pueski
 */
public class FilterPanel extends JPanel {
	
	private static final long serialVersionUID = 5616053764681382891L;

	public FilterPanel() {
		initComponents();
	}

	private void initComponents() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.pmedv.core.MessageResources");
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		filterTextField = new JXSearchField();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setLayout(new FormLayout(
			"131px:grow",
			"$lgap, default"));

		//---- filterTextField ----
		filterTextField.setPrompt("Filter");
		add(filterTextField, cc.xy(1, 2));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JXSearchField filterTextField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	public JXSearchField getFilterTextField() {
		return filterTextField;
	}

}
