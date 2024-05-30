package kb.gui;

import kb.ActivationHotKeyHandler;
import kb.utils.ExecutorServices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ExitActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame.getInstance().hideFrame();
		ActivationHotKeyHandler.getInstance().shutdown();
		ExecutorServices.shutdownEcexutorServices();
		System.exit(0);
	}
}
