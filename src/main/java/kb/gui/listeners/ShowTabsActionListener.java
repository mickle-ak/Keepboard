package kb.gui.listeners;

import kb.gui.MainFrame;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;

public class ShowTabsActionListener extends ViewMenuActionListener {

	public ShowTabsActionListener(JCheckBoxMenuItem checkBoxMenuItem) {
		super(checkBoxMenuItem, createAction());
	}

	private static Action createAction() {
		return new Action() {
			@Override
			public void adjustPreferences(boolean selected) {
				MyPreferences.INSTANCE.setTabsHidden(!selected);
			}
			
			@Override
			public void adjustMainFrame(boolean selected) {
				MainFrame.getInstance().setTabsVisible(selected);
			}
		};
	}
}
