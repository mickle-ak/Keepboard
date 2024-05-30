package kb.gui.prefs.platf;


public class WindowsAutostartHandler extends AutostartHandler {
	
	private boolean autostartSupported;
	
	public WindowsAutostartHandler() {
		autostartSupported = checkAutostartSupported();
	}
	
	private boolean checkAutostartSupported() {
		try {
			writeAutostartValue("kbtest", "kbtestv");
			deleteAutostartKey("kbtest");
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private void writeAutostartValue(String key, String value) throws Exception {
		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", 
				key, value);
	}
	
	private void deleteAutostartKey(String key) throws Exception {
		WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", key);
	}
	
	@Override
	public boolean isAutostartSupported() {
		return autostartSupported;
	}

	@Override
	public void setAutostart(boolean autostart) {
		try {
			doSetAutostart(autostart);
		} catch (Throwable e) {
			// Ignored
		}
	}
	
	private void doSetAutostart(boolean autostart) throws Exception {
		if (autostart) {
			writeAutostartValue("keepboard", getStartFilePath());
		} else {
			deleteAutostartKey("keepboard");
		}
	}
	
	private String getStartFilePath() {
		return ("\"" + System.getProperty("user.dir") + "\\Start.VBS\"").replace('/', '\\');
	}
}
