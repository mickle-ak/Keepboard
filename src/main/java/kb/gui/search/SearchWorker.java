package kb.gui.search;

import kb.ClipboardItem;
import kb.ItemGroupHolder;
import kb.NamedItemGroup;
import kb.TextClipboardItem;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SearchWorker {
	
	private final String searchText;
	private final boolean caseSensitive;
	private final SearchPanel searchPanel;
	
	private volatile boolean stopped = false;
	
	public SearchWorker(String searchText, boolean caseSensitive, SearchPanel searchPanel) {
		this.searchText = searchText;
		this.caseSensitive = caseSensitive;
		this.searchPanel = searchPanel;
	}
	
	public void search() {
		final List<Group> groups = getGroups();
		
		new Thread() {
			@Override
			public void run() {
				doSearch(groups);
			}
		}.start();
	}
	
	private List<Group> getGroups() {
		ItemGroupHolder itemGroupHolder = ItemGroupHolder.getInstance();
		List<Group> result = new ArrayList<Group>();
		
		result.add(new Group("", itemGroupHolder.getClipboardHistory().getItems()));
		
		for (NamedItemGroup namedItemGroup : itemGroupHolder.getItemGroups()) {
			result.add(new Group(namedItemGroup.getName(), namedItemGroup.getItemGroup().getItems()));
		}
		
		return result;
	}

	private void doSearch(List<Group> groups) {
		List<SearchResultItem> resultsPart = new ArrayList<SearchResultItem>();
		
		for (Group group : groups) {
			for (ClipboardItem clipboardItem : group.items) {
				if (stopped) {
					return;
				}
				
				if (isMatch(clipboardItem)) {
					resultsPart.add(new SearchResultItem(clipboardItem, group.name));
				}
				
				if (!resultsPart.isEmpty() && shouldPublishImmediately(clipboardItem)) {
					publishResultsPart(resultsPart, false);
					resultsPart = new ArrayList<SearchResultItem>();
				}
			}
		}
		
		publishResultsPart(resultsPart, true);
	}

	private boolean isMatch(ClipboardItem clipboardItem) {
		if (isMatch(clipboardItem.getName())) {
			return true;
		}
		
		if (!(clipboardItem instanceof TextClipboardItem)) {
			return false;
		}
		
		TextClipboardItem textClipboardItem = (TextClipboardItem) clipboardItem;
		
		return isMatch(textClipboardItem.getShortText()) || isMatch(textClipboardItem.getContent().getText());
	}

	private boolean isMatch(String string) {
		if (string == null) {
			return false;
		}
		
		if (caseSensitive) {
			return string.indexOf(searchText) != -1;
		} else {
			return string.toUpperCase().indexOf(searchText.toUpperCase()) != -1;
		}
	}

	private boolean shouldPublishImmediately(ClipboardItem clipboardItem) {
		return clipboardItem instanceof TextClipboardItem && clipboardItem.getFileName() != null;
	}

	private void publishResultsPart(final List<SearchResultItem> resultsPart, final boolean finished) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!SearchWorker.this.stopped) {
					searchPanel.publishResultsPart(resultsPart, finished);
				}
			}
		});
	}

	public void stop() {
		this.stopped = true;
	}
	
	private static class Group {
		final String name;
		final List<ClipboardItem> items;
		
		public Group(String name, List<ClipboardItem> items) {
			this.name = name;
			this.items = items;
		}
	}
}
