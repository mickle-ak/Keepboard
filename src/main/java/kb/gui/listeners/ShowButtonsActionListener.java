package kb.gui.listeners;

import kb.gui.MainFrame;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;

public class ShowButtonsActionListener extends ViewMenuActionListener {

	public ShowButtonsActionListener(JCheckBoxMenuItem checkBoxMenuItem) {
		super(checkBoxMenuItem, createAction());
	}

	private static Action createAction() {
		return new Action() {
			@Override
			public void adjustPreferences(boolean selected) {
				MyPreferences.INSTANCE.setButtonsHidden(!selected);
			}
			
			@Override
			public void adjustMainFrame(boolean selected) {
				MainFrame.getInstance().setButtonsVisible(selected);
			}
		};
	}
}
