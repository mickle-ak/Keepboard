package kb.clipboard;

import kb.ClipboardContent;
import kb.FileListClipboardContent;
import kb.ImageClipboardContent;
import kb.TextClipboardContent;
import kb.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;


class ClipboardReader {

	public ClipboardContent getClipboardContent(boolean readImage) {
		try {
			return readClipboardContent(readImage);
		} catch (Throwable e) {
			return null;
		}
	}

	private ClipboardContent readClipboardContent(boolean readImage) throws Exception {
		Transferable transferable = Utils.getClipboard().getContents(null);
		if (transferable != null) {
			return getContent(transferable, readImage);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ClipboardContent getContent(Transferable transferable, boolean readImage) throws Exception {
		if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return new FileListClipboardContent((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor), transferable);
		} else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return new TextClipboardContent((String) transferable.getTransferData(DataFlavor.stringFlavor));
		} else if (readImage && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			return readImageContent(transferable);
		}
		return null;
	}

	private ClipboardContent readImageContent(Transferable transferable) throws Exception {
		ImageIcon icon = new ImageIcon((Image) transferable.getTransferData(DataFlavor.imageFlavor));
		if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
			return null;
		}
		return new ImageClipboardContent(icon.getImage());
	}
}