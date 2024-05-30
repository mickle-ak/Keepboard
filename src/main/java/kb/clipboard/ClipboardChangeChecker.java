package kb.clipboard;

import com.sun.jna.Platform;

public abstract class ClipboardChangeChecker {
	private static ClipboardChangeChecker INSTANCE;
	
	public abstract boolean isClipboardChanged();
	
	public static ClipboardChangeChecker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createInstance();
		}
		return INSTANCE;
	}

	private static ClipboardChangeChecker createInstance() {
		try {
			return createPlatformSpecificInstance();
		} catch (Throwable e) {
			return new DefaultClipboardChangeChecker();
		}
	}

	private static ClipboardChangeChecker createPlatformSpecificInstance() {
		if (Platform.isWindows()) {
			return new WindowsClipboardChangeChecker();
		} else {
			return new DefaultClipboardChangeChecker();
		}
	}
}
