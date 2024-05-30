package kb.utils;

import java.util.Observable;

public class MyObservableImpl extends Observable {

	public void setChangedAndNotifyObservers() {
		setChanged();
		super.notifyObservers();
	}
}
