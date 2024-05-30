package kb.gui;

import kb.ClipboardContent;
import kb.ClipboardItem;
import kb.SelectedClipboardContent;
import kb.utils.Observable;

import javax.swing.*;

public abstract class MainFramePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Observable<SelectedClipboardContent> approvalObservable = new Observable<SelectedClipboardContent>();
	
	private Observable<ClipboardItem> selectionObservable = new Observable<ClipboardItem>();
	
	public MainFramePanel() {
		// Empty constructor
	}

	public final Observable<SelectedClipboardContent> getApprovalObservable() {
		return approvalObservable;
	}
	
	public final Observable<ClipboardItem> getSelectionObservable() {
		return selectionObservable;
	}
	
	public abstract void moveSelectedToTop();
	
	public abstract void selectNext();
	
	public abstract void selectPrevious();
	
	public abstract ClipboardContent getSelectedClipboardItemContent();
	
	public void panelDisplayed() {
		// Override if needed
	}
	
	public abstract ClipboardItem getSelectedItem();
	
	protected static void moveSelection(JTable table, int step) {
		int selectedIndex = table.getSelectedRow();
		if (selectedIndex >= 0) {
			int newSelectedIndex = selectedIndex + step;
			if (newSelectedIndex >= 0 && newSelectedIndex < table.getRowCount()) {
				table.getSelectionModel().setSelectionInterval(newSelectedIndex, newSelectedIndex);
				scrollToRow(table, newSelectedIndex);
			}
		}
	}
	
	protected static void scrollToRow(JTable table, int row) {
		table.scrollRectToVisible(table.getCellRect(row, 0, false));
	}

	public void moveItemUp() {
		// Override if needed
	}

	public void moveItemDown() {
		// Override if needed
	}

	public void copySelectedItemsToGroup() {
		// Override if needed
	}

	public void deleteSelectedItems() {
		// Override if needed
	}

	public void openSelectedItem() {
		// Override if needed
	}

	public void createNewItem() {
		// Override if needed
	}
}
