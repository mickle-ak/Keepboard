package kb.gui.search;

import kb.ClipboardItem;

public class SearchResultItem {
	
	private final ClipboardItem clipboardItem;
	private final String groupName;
	
	public SearchResultItem(ClipboardItem clipboardItem, String groupName) {
		this.clipboardItem = clipboardItem;
		this.groupName = groupName;
	}

	public ClipboardItem getClipboardItem() {
		return clipboardItem;
	}

	public String getGroupName() {
		return groupName;
	}
}
