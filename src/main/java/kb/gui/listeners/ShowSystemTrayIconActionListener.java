package kb.gui.listeners;

import kb.gui.SystemTrayHandler;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;

public class ShowSystemTrayIconActionListener extends ViewMenuActionListener {

	public ShowSystemTrayIconActionListener(JCheckBoxMenuItem checkBoxMenuItem) {
		super(checkBoxMenuItem, createAction());
	}

	private static Action createAction() {
		return new Action() {
			@Override
			public void adjustPreferences(boolean selected) {
				MyPreferences.INSTANCE.setTrayIconHidden(!selected);
			}
			
			@Override
			public void adjustMainFrame(boolean selected) {
				SystemTrayHandler.getInstance().setTrayIconVisible(selected);
			}
		};
	}
}
