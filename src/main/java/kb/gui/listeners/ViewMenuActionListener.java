package kb.gui.listeners;

import kb.PreferencesPersistor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ViewMenuActionListener implements ActionListener {

	private final JCheckBoxMenuItem checkBoxMenuItem;
	private final Action action;
	
	public ViewMenuActionListener(JCheckBoxMenuItem checkBoxMenuItem, Action action) {
		this.checkBoxMenuItem = checkBoxMenuItem;
		this.action = action;
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		action.adjustPreferences(checkBoxMenuItem.isSelected());
		new PreferencesPersistor().storeToDisk();
		action.adjustMainFrame(checkBoxMenuItem.isSelected());
	}
	
	protected static interface Action {
		
		void adjustPreferences(boolean selected);
		
		void adjustMainFrame(boolean selected); 
	}
}
