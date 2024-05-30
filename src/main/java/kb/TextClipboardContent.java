package kb;

import kb.utils.ObjectIO;

import java.awt.datatransfer.Transferable;

public class TextClipboardContent implements ClipboardContent {
	
	private volatile String text;
	private volatile String shortText;
	private volatile String fileName;
	private volatile Transferable nativeTransferable;
	
	public TextClipboardContent(String text) {
		this.text = text;
	}
	
	public static TextClipboardContent createProxy(String fileName, String shortText) {
		TextClipboardContent result = new TextClipboardContent(null);
		result.fileName = fileName;
		result.shortText = shortText;
		return result;
	}

	public String getText() {
		if (text == null) {
			// Multiple lazy initialization is acceptable
			text = new ObjectIO().readText(fileName);
			if (text == null) {
				// File may be damaged or deleted
				text = shortText;
			}
		}
		return text;
	}
	
	public Transferable getNativeTransferable() {
		return nativeTransferable;
	}

	public void setNativeTransferable(Transferable nativeTransferable) {
		this.nativeTransferable = nativeTransferable;
	}

	@Override
	public int hashCode() {
		getText();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		getText();
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		TextClipboardContent other = (TextClipboardContent) obj;
		
		other.getText();
		
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
}
