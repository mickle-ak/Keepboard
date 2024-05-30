package kb.clipboard;

import com.sun.jna.Platform;
import kb.*;
import kb.gui.Key;
import kb.gui.prefs.MyPreferences;
import kb.utils.ExecutorServices;
import kb.utils.Utils;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ClipboardAcessor {
	private static ClipboardAcessor INSTANCE;
	
	private ClipboardContent lastClipboardContent = null;
	private ClipboardChangeChecker clipboardChangeChecker;
	
	private ClipboardAcessor() {
		initClipboardChangeChecker();
	}
	
	public static void init() {
		ExecutorServices.CLIPBOARD_ACCESSOR_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				getInstance();
			}
		});
	}

	private void initClipboardChangeChecker() {
		clipboardChangeChecker = ClipboardChangeChecker.getInstance();
	}
	
	private void checkClipboard() {
		if (clipboardChangeChecker.isClipboardChanged()) {
			ClipboardContent content = getClipboardContent();
			if (updateLastClipboardContent(content)) {
				notifyClipboardManager(content);
			}
		}
	}

	private boolean updateLastClipboardContent(ClipboardContent clipboardContent) {
		if (clipboardContent != null && !isContentEmpty(clipboardContent)) {
			if (lastClipboardContent == null || !equalsToLast(clipboardContent)) {
				lastClipboardContent = clipboardContent;
				return true;
			}
		}
		
		return false;
	}

	private boolean equalsToLast(ClipboardContent clipboardContent) {
		if (lastClipboardContent.getClass() != clipboardContent.getClass()) {
			return false;
		}
		
		if (clipboardContent instanceof ImageClipboardContent && lastClipboardContent instanceof ImageClipboardContent) {
			return new ImageEqualityChecker().areEquals(((ImageClipboardContent) lastClipboardContent).getImage(),
					((ImageClipboardContent) clipboardContent).getImage());
		}
		
		return lastClipboardContent.equals(clipboardContent);
	}

	private boolean isContentEmpty(ClipboardContent clipboardContent) {
		return clipboardContent instanceof TextClipboardContent
				&& ((TextClipboardContent) clipboardContent).getText().trim().isEmpty();
	}

	private ClipboardContent getClipboardContent() {
		return new ClipboardReader().getClipboardContent(MyPreferences.INSTANCE.isAutosaveImages());
	}
	
	private void notifyClipboardManager(final ClipboardContent clipboardContent) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClipboardManager.getInstance().clipboardChanged(clipboardContent);
			}
		});
	}
	
	private void writeToClipboard(ClipboardContent content, boolean sendPaste) {
		ClipboardWriteTAE clipboardWriteTAE = new ClipboardWriteTAE(TransferableBuilder.createTransferable(content));
		clipboardWriteTAE.execute();
		
		if (clipboardWriteTAE.wasOperationSuccessful()) {
			afterSuccessfulWrite(content, sendPaste);
		}
	}
	
	private void afterSuccessfulWrite(ClipboardContent content, boolean sendPaste) {
		if (sendPaste) {
			Utils.sleepIgnoreInterrupt(50);
			sendPaste();
		}
		
		updateLastClipboardContent(content);
	}
	
	private void sendPaste() {
		Key autopasteKey = MyPreferences.INSTANCE.getAutopasteKey();
		if (RobotSupport.isRobotSupported() && autopasteKey != null) {
			if (Platform.isMac()) {
				RobotSupport.typeKeyAndWait(MyPreferences.INSTANCE.getFocusActiveWindowKey());
				Utils.sleepIgnoreInterrupt(100);
			}
			RobotSupport.typeKeyAndWait(autopasteKey);
		}
	}

	public static void writeToClipboard(ClipboardContent content) {
		submitWriteToClipboard(content, false);
	}
	
	private static Future<?> submitWriteToClipboard(final ClipboardContent content, final boolean sendPaste) {
		
		return ExecutorServices.CLIPBOARD_ACCESSOR_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				getInstance().writeToClipboard(content, sendPaste);
			}
		});
	}

	public static void checkForClipboardChangeAndWait() {
		try {
			submitClipboardChangeCheck().get();
		} catch (Exception ignored) {
			// Ignored
		}
	}
	
	public static void checkForClipboardChange() {
		submitClipboardChangeCheck();
	}

	private static Future<?> submitClipboardChangeCheck() {
		return ExecutorServices.CLIPBOARD_ACCESSOR_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				getInstance().checkClipboard();
			}
		});
	}
	
	private static ClipboardAcessor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ClipboardAcessor();
		}
		return INSTANCE;
	}

	public static void writeToClipboardAndPaste(ClipboardContent content) {
		submitWriteToClipboard(content, true);
	}

	public static String getClipboardText() {
		try {
			return submitClipboardTextReading().get(3, TimeUnit.SECONDS);
		} catch (Exception ignored) {
			return null;
		}
	}

	private static Future<String> submitClipboardTextReading() {
		return ExecutorServices.CLIPBOARD_ACCESSOR_EXECUTOR_SERVICE.submit(new Callable<String>() {
			@Override
			public String call() {
				return getInstance().doGetClipboardText();
			}
		});
	}

	private String doGetClipboardText() {
		ClipboardContent clipboardContent = getClipboardContent();
		if (clipboardContent instanceof TextClipboardContent) {
			return ((TextClipboardContent) clipboardContent).getText();
		}
		return null;
	}
}
