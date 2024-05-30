package kb.gui.search;

import kb.ClipboardContent;
import kb.ClipboardItem;
import kb.gui.MainFramePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchPanel extends MainFramePanel {
	private static final long serialVersionUID = 1L;
	
	private SearchInputPanel searchInputPanel;
	private SearchResultsPanel searchResultsPanel;
	
	private SearchWorker searchWorker;
	private int itemsFound;
	
	public SearchPanel() {
		createComponents();
		addComponents();
	}

	private void createComponents() {
		searchInputPanel = new SearchInputPanel();
		searchResultsPanel = new SearchResultsPanel(this);
		
		searchInputPanel.setSearchPanel(this);
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addSearchInputPanel();
		addSeparator();
		addSearchResultsPanel();
	}

	private void addSearchInputPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(searchInputPanel, c);
	}
	
	private void addSeparator() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 3, 5, 3);
		add(new JSeparator(), c);
	}

	private void addSearchResultsPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 2;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(searchResultsPanel, c);
	}
	
	public void startSearch(String searchText, boolean caseSensitive) {
		itemsFound = 0;
		searchResultsPanel.clearResults();
		searchWorker = new SearchWorker(searchText, caseSensitive, this);
		searchWorker.search();
	}
	
	public void stopSearch() {
		if (searchWorker == null) {
			return;
		}
		
		itemsFound = 0;
		searchWorker.stop();
		searchWorker = null;
	}
	
	public void publishResultsPart(List<SearchResultItem> resultsPart, boolean finished) {
		searchResultsPanel.appendResultsPart(resultsPart);
		itemsFound += resultsPart.size();
		
		if (finished) {
			searchInputPanel.searchingFinished(itemsFound);
		}
	}

	@Override
	public void moveSelectedToTop() {
		// Nothing is done for search panel.
	}

	@Override
	public void selectNext() {
		moveSelection(searchResultsPanel.getTable(), 1);
	}

	@Override
	public void selectPrevious() {
		moveSelection(searchResultsPanel.getTable(), -1);
	}

	@Override
	public ClipboardContent getSelectedClipboardItemContent() {
		return searchResultsPanel.getSelectedClipboardItemContent();
	}

	@Override
	public ClipboardItem getSelectedItem() {
		return searchResultsPanel.getSelectedItem();
	}

	@Override
	public void panelDisplayed() {
		searchInputPanel.focusTextField();
	}
	
	public SearchResultsPanel getSearchResultsPanel() {
		return searchResultsPanel;
	}
}
