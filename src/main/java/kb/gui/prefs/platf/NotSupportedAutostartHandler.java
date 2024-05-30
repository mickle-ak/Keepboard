package kb.gui.prefs.platf;

public class NotSupportedAutostartHandler extends AutostartHandler {

	@Override
	public boolean isAutostartSupported() {
		return false;
	}

	@Override
	public void setAutostart(boolean autostart) {
		// Ignored
	}
}
