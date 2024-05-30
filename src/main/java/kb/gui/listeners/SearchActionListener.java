package kb.gui.listeners;

import kb.gui.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().showSearchPane();
	}
}
