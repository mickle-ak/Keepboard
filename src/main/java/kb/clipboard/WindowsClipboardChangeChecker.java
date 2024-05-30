package kb.clipboard;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;


public class WindowsClipboardChangeChecker extends ClipboardChangeChecker {
	
	private volatile int sequnceNumberJNAResult;
	
	private int sequenceNumber;
	
	public WindowsClipboardChangeChecker() {
		sequenceNumber = getSequenceNumber() - 1;
	}

    private int getSequenceNumber() {
    	try {
			SwingUtilities.invokeAndWait(createSequenceNumberRunnable());
			return sequnceNumberJNAResult;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return 0;
		} catch (InvocationTargetException e) {
			return 0;
		}
	}

	private Runnable createSequenceNumberRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				sequnceNumberJNAResult = JNAUtils.getClipboardSequenceNumber();
			}
		};
	}

    @Override
	public boolean isClipboardChanged() {
    	int currentSequenceNumber = getSequenceNumber();
		if (sequenceNumber != currentSequenceNumber) {
			sequenceNumber = currentSequenceNumber;
			return true;
		}
		return false;
	}
}