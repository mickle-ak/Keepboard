package kb.gui.listeners;

import kb.gui.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoToClipboardHistoryActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().goToClipboardHistory();
	}
}