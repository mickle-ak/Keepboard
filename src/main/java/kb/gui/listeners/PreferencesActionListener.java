package kb.gui.listeners;

import kb.gui.prefs.PreferencesDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreferencesActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent arg0) {
		PreferencesDialog.showDialog();
	}
}