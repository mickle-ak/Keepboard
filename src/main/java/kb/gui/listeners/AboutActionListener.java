package kb.gui.listeners;

import kb.gui.AboutDialog;
import kb.gui.AboutPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		new AboutDialog(AboutPanel.FocusedTab.LICENSE);
	}
}