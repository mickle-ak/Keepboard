package kb;

import kb.utils.Observable;

import java.util.*;

public class ItemGroup {
	
	private final LinkedList<ClipboardItem> items;
	
	private final Observable<List<ClipboardItem>> observable = new Observable<List<ClipboardItem>>();
	
	private final String fileName;
	
	private final ListPersistor<ClipboardItem> itemsPersistor = new ClipboardHistoryPersistor();
	
	private int maxSize = 10000;
	
	public ItemGroup(String fileName) {
		this.fileName = fileName;
		this.items = new LinkedList<ClipboardItem>(itemsPersistor.readFromDisk(fileName));
	}
	
	private void addNewItem(ClipboardItem clipboardItem) {
		items.addFirst(clipboardItem);
		deleteOutOfGroupSizeItem();
		itemsChanged();
	}

	private void deleteOutOfGroupSizeItem() {
		if (items.size() > maxSize) {
			items.removeLast().deletePersistedContent();
		}
	}

	public void itemsChanged() {
		storeToDisk();
		observable.updateObservers(getItems());
	}

	public List<ClipboardItem> getItems() {
		return new ArrayList<ClipboardItem>(items);
	}
	
	private void storeToDisk() {
		itemsPersistor.storeToDisc(items, fileName);
	}

	private boolean moveToTopIfExists(ClipboardItem clipboardItem) {
		ClipboardItem existingItem = removeItemIfExisits(clipboardItem);
		
		if (existingItem != null) {
			items.addFirst(existingItem);
			itemsChanged();
			return true;
		}
		
		return false;
	}
	
	private ClipboardItem removeItemIfExisits(ClipboardItem clipboardItem) {
		ClipboardContent content = clipboardItem.getContent();
		for (Iterator<ClipboardItem> it = items.iterator(); it.hasNext(); ) {
			ClipboardItem existingClipboardItem = it.next();
			if (existingClipboardItem.equalsByValue(clipboardItem, content)) {
				it.remove();
				existingClipboardItem.setNativeTransferable(clipboardItem.getNativeTransferable());
				return existingClipboardItem;
			}
		}
		
		return null;
	}
	
	public void moveExistingItemToTop(ClipboardItem clipboardItem) {
		removeExistingItem(clipboardItem);
		items.addFirst(clipboardItem);
		deleteOutOfGroupSizeItem(); // TODO
		itemsChanged();
	}
	
	private ClipboardItem removeExistingItem(ClipboardItem clipboardItem) {
		for (Iterator<ClipboardItem> it = items.iterator(); it.hasNext(); ) {
			ClipboardItem existingClipboardItem = it.next();
			if (existingClipboardItem == clipboardItem) { // Intentional comparing by reference
				it.remove();
				return existingClipboardItem;
			}
		}
		return null;
	}
	
	public Observable<List<ClipboardItem>> getObservable() {
		return observable;
	}
	
	public void deleteItem(ClipboardItem clipboardItem) {
		ClipboardItem existingItem = removeExistingItem(clipboardItem);
		if (existingItem != null) {
			existingItem.deletePersistedContent();
			itemsChanged();
		}
	}
	
	public void addOrMoveItemToTop(ClipboardItem clipboardItem) {
		if (moveToTopIfExists(clipboardItem)) {
			clipboardItem.deletePersistedContent();
		} else {
			addNewItem(clipboardItem);
		}
	}
	
	public void replaceItem(ClipboardItem itemToBeReplaced, ClipboardItem newItem) {
		int itemToRemoveIndex = getItemIndex(itemToBeReplaced);
		
		if (itemToRemoveIndex >= 0) {
			items.set(itemToRemoveIndex, newItem);
			itemToBeReplaced.deletePersistedContent();
			itemsChanged();
		}
	}
	
	private int getItemIndex(ClipboardItem item) {
		int result = 0;
		for (ClipboardItem current : items) {
			if (current == item) { // Intentional comparing by reference
				return result;
			}
			result++;
		}
		return -1;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void moveUp(ClipboardItem clipboardItem) {
		for (ListIterator<ClipboardItem> iterator = items.listIterator(); iterator.hasNext(); ) {
			ClipboardItem existingClipboardItem = iterator.next();
			if (clipboardItem == existingClipboardItem) {
				iterator.remove();
				if (iterator.hasPrevious()) {
					iterator.previous();
				}
				iterator.add(existingClipboardItem);
				itemsChanged();
				break;
			}
		}
	}

	public void moveDown(ClipboardItem clipboardItem) {
		for (ListIterator<ClipboardItem> iterator = items.listIterator(); iterator.hasNext(); ) {
			ClipboardItem existingClipboardItem = iterator.next();
			if (clipboardItem == existingClipboardItem) {
				iterator.remove();
				if (iterator.hasNext()) {
					iterator.next();
				}
				iterator.add(existingClipboardItem);
				itemsChanged();
				break;
			}
		}
	}
	
	public void setMaxGroupSize(int maxSize) {
		if (this.maxSize != maxSize) {
			this.maxSize = maxSize;
			int itemsToBeDeletedCount = items.size() - maxSize; 
			for (int i = 0; i < itemsToBeDeletedCount; i++) {
				items.removeLast().deletePersistedContent();
			}
			if (itemsToBeDeletedCount > 0) {
				itemsChanged();
			}
		}
	}
}