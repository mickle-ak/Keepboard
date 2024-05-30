package kb.gui.listeners;

import kb.gui.WhatsNewDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhatsNewActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		new WhatsNewDialog();
	}
}