package kb.gui.listeners;

import kb.clipboard.ClipboardAcessor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveClipboardContentActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		ClipboardAcessor.checkForClipboardChange();
	}
}