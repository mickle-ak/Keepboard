package kb;

import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.HashSet;
import java.util.List;

public class FileListClipboardContent implements ClipboardContent {
	
	private final List<File> files;
	private final Transferable nativeTransferable;
	
	public FileListClipboardContent(List<File> files, Transferable nativeTransferable) {
		this.files = files;
		this.nativeTransferable = nativeTransferable;
	}

	public List<File> getFiles() {
		return files;
	}
	
	public Transferable getNativeTransferable() {
		return nativeTransferable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((files == null) ? 0 : new HashSet<File>(files).hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileListClipboardContent other = (FileListClipboardContent) obj;
		if (files == null) {
			if (other.files != null)
				return false;
		} else if (!new HashSet<File>(files).equals(new HashSet<File>(other.files)))
			return false;
		return true;
	}
}
