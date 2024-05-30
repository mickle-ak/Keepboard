package kb.gui.listeners;

import kb.ClipboardChangeListener;
import kb.PreferencesPersistor;
import kb.gui.MainFrame;
import kb.gui.SystemTrayHandler;
import kb.gui.prefs.MyPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean paused = ClipboardChangeListener.getInstance().isPaused();

		if (paused) {
			MainFrame.getInstance().continueClipboardChangeTracking();
		} else {
			MainFrame.getInstance().pauseClipboardChangeTracking();
		}

		paused = !paused;

		MyPreferences.INSTANCE.setPaused(paused);
		new PreferencesPersistor().storeToDisk();
		SystemTrayHandler.getInstance().setTrayIcon(paused);
	}
}