package kb.clipboard;

public class DefaultClipboardChangeChecker extends ClipboardChangeChecker {

	@Override
	public boolean isClipboardChanged() {
		return true;
	}
}
