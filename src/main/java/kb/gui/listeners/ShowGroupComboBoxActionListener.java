package kb.gui.listeners;

import kb.gui.MainFrame;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;

public class ShowGroupComboBoxActionListener extends ViewMenuActionListener {

	public ShowGroupComboBoxActionListener(JCheckBoxMenuItem checkBoxMenuItem) {
		super(checkBoxMenuItem, createAction());
	}

	private static Action createAction() {
		return new Action() {
			@Override
			public void adjustPreferences(boolean selected) {
				MyPreferences.INSTANCE.setGroupComboBoxHidden(!selected);
			}
			
			@Override
			public void adjustMainFrame(boolean selected) {
				MainFrame.getInstance().setGroupComboBoxVisible(selected);
			}
		};
	}
}
