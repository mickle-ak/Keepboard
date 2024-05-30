package kb.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class TrialAndErrorOperation<T> {
	private final int trialCount;
	
	private List<Throwable> exceptions = new ArrayList<Throwable>();
	
	private boolean operationSuccessful = false;
	
	public TrialAndErrorOperation(int trialCount) {
		this.trialCount = trialCount;
	}
	
	public final T execute() {
		for (int i = 0; i < trialCount; i++) {
			try {
				T result = executeOperation();
				operationSuccessful = true;
				return result;
			} catch (Throwable e) {
				exceptions.add(e);
				onFailure(e);
			}
		}
		
		return onOperationFailure();
	}
	
	protected void onFailure(Throwable e) {
		// Override if needed
	}
	
	protected T onOperationFailure() {
		// Override if needed
		return null;
	}
	
	public final List<Throwable> getExceptions() {
		return exceptions;
	}
	
	public final boolean wasOperationSuccessful() {
		return operationSuccessful;
	}

	protected abstract T executeOperation() throws Throwable;
}
