package kb;

import kb.utils.ObjectIO;

import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

public class TextClipboardItem extends ClipboardItem {
	
	private static final int MAX_SHORT_TEXT_LENGTH = 1024;
	
	private final String shortText;
	
	public TextClipboardItem(String displayText, String fileName, String name, String shortText) {
		super(displayText, fileName, name);
		this.shortText = shortText;
	}

	public TextClipboardItem(String content) {
		super(createDisplayText(content), createFile(content), null);
		this.shortText = createShortText(content);
	}
	
	public TextClipboardItem(List<File> files, Transferable nativeTransferable) {
		super(getFileNames(files), null, null);
		this.shortText = getFilePaths(files);
		setNativeTransferable(nativeTransferable);
	}
	
	private String getFilePaths(List<File> files) {
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(file.getAbsolutePath());
		}
		return sb.toString();
	}

	private static String getFileNames(List<File> files) {
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(file.getName());
		}
		return sb.toString();
	}

	private static String createFile(String content) {
		if (content.length() > MAX_SHORT_TEXT_LENGTH) {
			return storeInNewFile(content);
		}
		return null;
	}
	
	private static String storeInNewFile(String clipboardContents) {
		String fileName = getNewFileName();
		new ObjectIO().writeText(clipboardContents, fileName);
		return fileName;
	}

	private static String createShortText(String text) {
		return text.length() > MAX_SHORT_TEXT_LENGTH ? text.substring(0, MAX_SHORT_TEXT_LENGTH) : text;
	}

	private static String createDisplayText(String content) {
		return createShortText(content.replaceAll("\\s+", " ").trim()).trim();
	}
	
	public String getShortText() {
		return shortText;
	}
	
	@Override
	public TextClipboardContent getContent() {
		TextClipboardContent result = getFileName() == null ? new TextClipboardContent(shortText) 
			: TextClipboardContent.createProxy(getFileName(), shortText);
		result.setNativeTransferable(getNativeTransferable());
		return result;
	}

	@Override
	public boolean equalsByValue(ClipboardItem other, ClipboardContent otherContent) {
		if (!super.equalsByValue(other, otherContent)) {
			return false;
		}
		
		if (!(other instanceof TextClipboardItem)) {
			return false;
		}
		
		TextClipboardItem textItem = (TextClipboardItem) other;
		
		if (!this.shortText.equals(textItem.shortText)) {
			return false;
		}
		
		if (!this.getDisplayText().equals(textItem.getDisplayText())) {
			return false;
		}
		
		if (this.getFileName() == null && textItem.getFileName() == null) {
			return true;
		}
		
		if (this.getFileName() != null && textItem.getFileName() != null) {
			return this.getContent().equals(otherContent);
		}
		
		return false;
	}

	@Override
	public TextClipboardItem createClonedInstance() {
		TextClipboardItem result = new TextClipboardItem(getContent().getText());
		result.setName(getName());
		return result;
	}
}
