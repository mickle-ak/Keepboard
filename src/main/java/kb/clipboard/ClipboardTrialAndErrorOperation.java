package kb.clipboard;

import kb.utils.TrialAndErrorOperation;
import kb.utils.Utils;

abstract class ClipboardTrialAndErrorOperation<T> extends TrialAndErrorOperation<T> {
	private static final int MAX_CLIPBOARD_ACCESS_TRIALS = 10;
	
	private static final int AFTER_FAILURE_SLEEP_TIME = 200;
	
	protected ClipboardTrialAndErrorOperation() {
		super(MAX_CLIPBOARD_ACCESS_TRIALS);
	}

	@Override
	protected void onFailure(Throwable e) {
		Utils.sleepIgnoreInterrupt(AFTER_FAILURE_SLEEP_TIME);
	}
}