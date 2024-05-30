package kb.gui;

public enum ShortcutKeyCategory {
	APPLICATION("System-wide"),
	CLIPBOARD_MENU("Clipboard menu"),
	SAVED_ITEMS_MENU("Saved items menu"),
	TOOLS_MENU("Tools menu"),
	MAIN_FRAME("Main frame");
	
	private ShortcutKeyCategory(String description) {
		this.description = description;
	}
	
	private final String description;

	public String getDescription() {
		return description;
	}
}
