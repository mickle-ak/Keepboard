package kb.clipboard;

import kb.ClipboardContent;
import kb.ImageClipboardContent;
import kb.TextClipboardContent;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class TransferableBuilder {
	
	private TransferableBuilder() {
		// private constructor
	}
	
	public static Transferable createTransferable(ClipboardContent clipboardContent) {
		if (clipboardContent instanceof TextClipboardContent) {
			TextClipboardContent textClipboardContent = (TextClipboardContent) clipboardContent;
			if (textClipboardContent.getNativeTransferable() != null) {
				return textClipboardContent.getNativeTransferable();
			}
			return new StringSelection(textClipboardContent.getText());
		} else {
			return new ImageSelection(((ImageClipboardContent) clipboardContent).getImage());
		}
	}
	
	
}
