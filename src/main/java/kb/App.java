package kb;

import com.sun.jna.Platform;
import kb.clipboard.JNAUtils;
import kb.gui.SystemTrayHandler;
import kb.gui.prefs.MyPreferences;
import kb.gui.prefs.platf.AutostartHandler;
import kb.utils.ObjectIO;

import javax.swing.*;


public class App {
	public App() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				startApp();
			}
		});
	}

	private void startApp() {
		boolean firstTimeStart = ObjectIO.isFirstTimeStart();
		initJNA();
		reinitPreferencesDefaults();
		ClipboardManager.start();
		AutostartHandler.getInstance().init();
		SystemTrayHandler.getInstance().showInSystemTray();
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		if (firstTimeStart) {
			showMainFrame();
		}
	}

	private void reinitPreferencesDefaults() {
		if (MyPreferences.INSTANCE.getInstallationDate() == 0) {
			MyPreferences.INSTANCE.setInstallationDate(System.currentTimeMillis());
			new PreferencesPersistor().storeToDisk();
		}
	}

	private void initJNA() {
		if (Platform.isWindows()) {
			JNAUtils.getClipboardSequenceNumber();
		}
	}

	private void showMainFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClipboardManager.getInstance().showMainFrame();
			}
		});
	}
}
