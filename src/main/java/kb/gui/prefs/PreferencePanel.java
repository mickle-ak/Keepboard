package kb.gui.prefs;

import kb.utils.Observable;

import javax.swing.*;


public abstract class PreferencePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected Observable<PreferenceChangeState> changeObservable = new Observable<PreferenceChangeState>();
	
	public Observable<PreferenceChangeState> getChangeObservable() {
		return changeObservable;
	}
	
	public abstract void applyChanges();
	
	public abstract void clearChanges();
}
