package kb;

import kb.utils.ObjectIO;
import kb.utils.Utils;

import java.awt.datatransfer.Transferable;

public abstract class ClipboardItem {
	
	private static final String ITEM_SUFFIX = "item";
	
	private final String displayText;
	private final String fileName;
	private volatile String name;
	private volatile boolean deleted;
	private volatile Transferable nativeTransferable;
	
	public ClipboardItem(String displayText, String fileName, String name) {
		this.displayText = displayText;
		this.fileName = fileName;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void deletePersistedContent() {
		if (fileName != null) {
			new ObjectIO().delete(fileName);
		}
		deleted = true;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	protected final static String getNewFileName() {
		return FileNameGenerator.generate("history/", ITEM_SUFFIX);
	}
	
	public abstract ClipboardContent getContent();
	
	public boolean equalsByValue(ClipboardItem other, ClipboardContent otherContent) {
		if (Utils.areEqualsNullsIncluded(getName(), other.getName())) {
			return true;
		}
		
		String name = getName() == null ? "" : getName().trim();
		String otherName = other.getName() == null ? "" : other.getName().trim();
		return name.equals(otherName);
	}
	
	public abstract ClipboardItem createClonedInstance();
	
	public void setNativeTransferable(Transferable nativeTransferable) {
		this.nativeTransferable = nativeTransferable;
	}
	
	public Transferable getNativeTransferable() {
		return nativeTransferable;
	}
}
