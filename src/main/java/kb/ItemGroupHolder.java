package kb;

import kb.gui.prefs.MyPreferences;
import kb.utils.Observable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ItemGroupHolder {
	public static final String HISTORY_FILE_NAME = "history/dataholder.hstr";

	private static final ItemGroupHolder INSTANCE = new ItemGroupHolder();
	
	private ItemGroup clipboardHistory;
	private List<NamedItemGroup> groups;
	
	private NamedGroupsPersistor persistor = new NamedGroupsPersistor();
	
	private Observable<List<NamedItemGroup>> changeObservable = new Observable<List<NamedItemGroup>>();
	private Observable<NamedItemGroup> groupCreationObservable = new Observable<NamedItemGroup>();
	
	private ItemGroupHolder() {
		clipboardHistory = new ItemGroup(HISTORY_FILE_NAME);
		clipboardHistory.setMaxGroupSize(MyPreferences.INSTANCE.getMaxHistorySize());
		groups = persistor.readFromDisk("history/descriptors.hstr");
		initUsedFileNames();
	}
	
	private void initUsedFileNames() {
		initClipboardHistoryUsedFileNames();
		initGroupItemsUsedFileNames();
		initGroupsUsedFileNames();
	}
	
	private void initClipboardHistoryUsedFileNames() {
		addItemsUsedFileNames(clipboardHistory.getItems());
	}
	
	private void addItemsUsedFileNames(List<ClipboardItem> items) {
		for (ClipboardItem clipboardItem : items) {
			if (clipboardItem.getFileName() != null) {
				FileNameGenerator.addToReservedNames(clipboardItem.getFileName());
				if (clipboardItem instanceof ImageClipboardItem) {
					FileNameGenerator.addToReservedNames(((ImageClipboardItem) clipboardItem).getPreviewFileName());
				}
			}
		}
	}

	private void initGroupItemsUsedFileNames() {
		for (NamedItemGroup group : groups) {
			addItemsUsedFileNames(group.getItemGroup().getItems());
		}
	}

	private void initGroupsUsedFileNames() {
		for (NamedItemGroup group : groups) {
			FileNameGenerator.addToReservedNames(group.getItemGroup().getFileName());
		}
	}

	public Observable<List<NamedItemGroup>> getChangeObservable() {
		return changeObservable;
	}
	
	public Observable<NamedItemGroup> getGroupCreationObservable() {
		return groupCreationObservable;
	}
	
	public static final ItemGroupHolder getInstance() {
		return INSTANCE;
	}

	public ItemGroup getClipboardHistory() {
		return clipboardHistory;
	}

	public List<NamedItemGroup> getItemGroups() {
		return new ArrayList<NamedItemGroup>(groups);
	}
	
	public void addNewItemGroup(String name) throws UserException {
		checkItemGroupExists(name);
		NamedItemGroup group = new NamedItemGroup(name, FileNameGenerator.generate("history/", "igrp"));
		groups.add(0, group);
		storeToDisk();
		groupCreationObservable.updateObservers(group);
	}
	
	private void storeToDisk() {
		persistor.storeToDisc(groups, "history/descriptors.hstr");
	}

	private void checkItemGroupExists(String name) throws UserException {
		for (NamedItemGroup group : groups) {
			if (group.getName().equals(name)) {
				throw new UserException("Group '" + name + "' already exists.");
			}
		}
	}
	
	public void renameItemGroup(String oldName, String newName) throws UserException {
		checkItemGroupExists(newName);
		replaceItemGroup(oldName, newName);
		storeToDisk();
		changeObservable.updateObservers(getItemGroups());
	}

	private void replaceItemGroup(String oldName, String newName) {
		for (int i = 0; i < groups.size(); i++) {
			NamedItemGroup oldGroup = groups.get(i);
			if (oldGroup.getName().equals(oldName)) {
				NamedItemGroup newGroup = new NamedItemGroup(newName, oldGroup.getItemGroup());
				groups.set(i, newGroup);
				break;
			}
		}
	}
	
	public void deleteItemGroup(String name) {
		doDeleteItemGroup(name);
		storeToDisk();
		changeObservable.updateObservers(getItemGroups());
		if (groups.isEmpty() && !MyPreferences.INSTANCE.areAllGroupsManuallyDeleted()) {
			MyPreferences.INSTANCE.setAllGroupsManuallyDeleted(true);
			new PreferencesPersistor().storeToDisk();
		}
	}

	private void doDeleteItemGroup(String name) {
		for (Iterator<NamedItemGroup> it = groups.iterator(); it.hasNext(); ) {
			NamedItemGroup group = it.next();
			if (group.getName().equals(name)) {
				it.remove();
				removeItemsPersistedContent(group);
				persistor.delete(group.getItemGroup().getFileName());
				return;
			}
		}
	}
	
	private void removeItemsPersistedContent(NamedItemGroup group) {
		for (ClipboardItem clipboardItem : group.getItemGroup().getItems()) {
			clipboardItem.deletePersistedContent();
		}
	}

	public ItemGroup getItemGroup(String groupName) {
		for (NamedItemGroup namedItemGroup : getItemGroups()) {
			if (namedItemGroup.getName().equals(groupName)) {
				return namedItemGroup.getItemGroup();
			}
		}
		return null;
	}
	
	public void moveGroupToTop(String name) {
		for (Iterator<NamedItemGroup> iterator = groups.iterator(); iterator.hasNext(); ) {
			NamedItemGroup group = iterator.next();
			if (group.getName().equals(name)) {
				iterator.remove();
				groups.add(0, group);
				storeToDisk();
				changeObservable.updateObservers(getItemGroups());
				return;
			}
		}
	}
}
