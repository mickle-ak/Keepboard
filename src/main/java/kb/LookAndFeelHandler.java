package kb;

import kb.gui.MainFrame;
import kb.gui.prefs.LookAndFeelPref;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;


public class LookAndFeelHandler {
	public static final LookAndFeelHandler INSTANCE = new LookAndFeelHandler();
	
	private LookAndFeelHandler() {
		// private constructor
	}
	
	public synchronized boolean isNimbusSupported() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    if ("Nimbus".equals(info.getName())) {
		        return true;
		    }
		}
		return false;
	}
	
	public synchronized void setLookAndFeel(LookAndFeelPref lookAndFeelPref) {
		switch (lookAndFeelPref) {
		case NIMBUS:
			setNimbusLookAndFeel();
			break;
		case SYSTEM:
			setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			break;
		case METAL:
			setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			break;
		}
	}

	private void setNimbusLookAndFeel() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    if ("Nimbus".equals(info.getName())) {
		        setLookAndFeel(info.getClassName());
		    }
		}
	}
	
	private void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			// Ignored
		}
	}
	
	public void updateLookAndFeel(LookAndFeelPref lookAndFeelPref) {
		setLookAndFeel(lookAndFeelPref);
		MainFrame.reinitMainFrame();
	}
}
