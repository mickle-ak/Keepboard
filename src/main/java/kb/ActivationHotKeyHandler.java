package kb;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;

public class ActivationHotKeyHandler {
	
	private static final ActivationHotKeyHandler INSTANCE = new ActivationHotKeyHandler();
	
	private static Provider provider;
	
	public static ActivationHotKeyHandler getInstance() {
		return INSTANCE;
	}
	
	private ActivationHotKeyHandler() {
		try {
			provider = Provider.getCurrentProvider(true);
		} catch (Throwable t) {
			// Ignored
		}
	}
	
	public void registerActivationHotKey(KeyStroke keyStroke) {
		if (provider == null) {
			return;
		}
		
		provider.reset();
		
		if (keyStroke != null) {
			provider.register(keyStroke, new HotKeyListener() {
				@Override
				public void onHotKey(HotKey arg0) {
					hotKeyPressed();
				}
			});
		}
	}
	
	private void hotKeyPressed() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClipboardManager.getInstance().showMainFrame();
			}
		});
	}
	
	public void shutdown() {
		if (provider == null) {
			return;
		}
		
		try {
			provider.reset();
			provider.stop();
		} catch (Throwable ignored) {
			// Ignored
		}
	}
}
