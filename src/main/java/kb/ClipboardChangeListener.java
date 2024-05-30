package kb;

import kb.clipboard.ClipboardAcessor;
import kb.utils.ExecutorServices;

import java.util.concurrent.Semaphore;


public final class ClipboardChangeListener {
	private static final long CLIPBOARD_CHANGE_CHECK_INTERVAL = 550;
	
	private static final ClipboardChangeListener INSTANCE = new ClipboardChangeListener();
	
	private final Semaphore semaphore = new Semaphore(1); 
	
	private volatile boolean paused = false;
	
	private volatile boolean started = false;
	
	private ClipboardChangeListener() {
		// private constructor
	}
	
	public static ClipboardChangeListener getInstance() {
		return INSTANCE;
	}
	
	public synchronized void startListening() {
		if (started) {
			throw new IllegalStateException("Clipboard change listener is already started.");
		}
		
		started = true;
		
		submitClipboardChangeListening();
	}

	private void submitClipboardChangeListening() {
		ExecutorServices.CLIPBOARD_CHANGE_LISTENER_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(CLIPBOARD_CHANGE_CHECK_INTERVAL);
						waitIfPaused();
					} catch (InterruptedException e) {
						return;
					}
					
					ClipboardAcessor.checkForClipboardChangeAndWait();
				}
			}
		});
	}
	
	private void waitIfPaused() throws InterruptedException {
		try {
			semaphore.acquire();
		} finally {
			semaphore.release();
		}
	}

	public synchronized void pauseListening() {
		if (paused) {
			throw new IllegalStateException("Clipboard change listener is already paused.");
		}
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		paused = true;
	}
	
	public synchronized void continueListening() {
		if (!paused) {
			throw new IllegalStateException("Clipboard change listener is not paused.");
		}
		
		semaphore.release();
		
		paused = false;
	}
	
	public boolean isPaused() {
		return paused;
	}
}
