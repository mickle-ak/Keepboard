package kb.gui.listeners;

import kb.gui.MainFrame;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;

public class ShowToolbarActionListener extends ViewMenuActionListener {

	public ShowToolbarActionListener(JCheckBoxMenuItem checkBoxMenuItem) {
		super(checkBoxMenuItem, createAction());
	}

	private static Action createAction() {
		return new Action() {
			@Override
			public void adjustPreferences(boolean selected) {
				MyPreferences.INSTANCE.setToolbarHidden(!selected);
			}
			
			@Override
			public void adjustMainFrame(boolean selected) {
				MainFrame.getInstance().setToolbarVisible(selected);
			}
		};
	}
}
