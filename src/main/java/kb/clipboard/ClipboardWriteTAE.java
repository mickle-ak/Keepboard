package kb.clipboard;

import kb.utils.Utils;

import java.awt.datatransfer.Transferable;


class ClipboardWriteTAE extends ClipboardTrialAndErrorOperation<Void> {
	
	private Transferable contents;
	
	public ClipboardWriteTAE(Transferable contents) {
		this.contents = contents;
	}

	@Override
	protected Void executeOperation() throws Throwable {
		Utils.getClipboard().setContents(contents, null);
		return null;
	}
}