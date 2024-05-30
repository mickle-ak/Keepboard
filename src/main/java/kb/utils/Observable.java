package kb.utils;

import java.util.ArrayList;
import java.util.Collection;

public class Observable<D> {
	
	private Collection<Observer<D>> observers = new ArrayList<Observer<D>>();
	
	public void addObserver(Observer<D> observer) {
		observers.add(observer);
	}
	
	public void updateObservers(D data) {
		for (Observer<D> observer : observers) {
			observer.update(data);
		}
	}
	
	public void removeObserver(Observer<D> observer) {
		observers.remove(observer);
	}
}
