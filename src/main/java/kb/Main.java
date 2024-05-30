package kb;

import kb.gui.prefs.LookAndFeelPref;
import kb.gui.prefs.MyPreferences;
import kb.utils.ObjectIO;

public class Main {
	public static void main(String[] args) {
		if (ObjectIO.takeLock()) {
			loadPreferences();
			setLookAndFeel(); 
			new App();
		}
	}

	private static void loadPreferences() {
		new PreferencesPersistor().loadFromDisk();
	}

	private static void setLookAndFeel() {
		checkIsNimbusSetAndSupported();
		LookAndFeelHandler.INSTANCE.setLookAndFeel(MyPreferences.INSTANCE.getLookAndFeelPref());
	}

	private static void checkIsNimbusSetAndSupported() {
		if (MyPreferences.INSTANCE.getLookAndFeelPref() == LookAndFeelPref.NIMBUS
				&& !LookAndFeelHandler.INSTANCE.isNimbusSupported()) {
			MyPreferences.INSTANCE.setLookAndFeelPref(LookAndFeelPref.SYSTEM);
		}
	}
}