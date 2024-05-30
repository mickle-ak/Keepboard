package kb.gui.prefs.platf;

import com.sun.jna.Platform;
import kb.gui.prefs.MyPreferences;

public abstract class AutostartHandler {
	private static AutostartHandler INSTANCE;
	
	public abstract boolean isAutostartSupported();
	
	public abstract void setAutostart(boolean autostart);
	
	public static AutostartHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createInstance();
		}
		return INSTANCE;
	}
	
	public final void init() {
		if (isAutostartSupported()) {
			boolean autostart = MyPreferences.INSTANCE.isAutostart();
			setAutostart(!autostart);
			setAutostart(autostart);
		}
	}

	private static AutostartHandler createInstance() {
		if (Platform.isX11()) {
			return new X11AutostartHandler();
		} else if (Platform.isWindows()) {
			return new WindowsAutostartHandler();
		}
		
		return new NotSupportedAutostartHandler();
	}
}
