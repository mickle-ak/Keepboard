package kb.gui;

public class WhatsNewContent {
	public static String getContent() {
		return  "Version 5.7\n"
				+ "-------------------\n"
				+ "- Added ability to package Keepboard in Snap.\n\n"

				+ "Version 5.6\n"
				+ "-------------------\n"
				+ "- Added XMR as a donation method.\n"
				+ "- Fixed some compatibility issues with newer Java versions.\n\n"

				+ "Version 5.5\n"
				+ "-------------------\n"
				+ "- Fixed startup issue in newer Java versions.\n\n"

				+ "Version 5.4\n"
				+ "-------------------\n"
				+ "- If SHIFT is also pressed, the selected item is only put to the clipboard without auto-pasting.\n\n"

				+ "Version 5.3\n"
				+ "-------------------\n"
				+ "- Fixed startup issue that was occurring in some Linux environments.\n"
				+ "- Added donate panel.\n\n"

				+ "Version 5.2\n"
				+ "-------------------\n"
				+ "- Added configurable shortcut key to close search tab (Ctrl + W by default).\n\n"

				+"Version 5.1\n"
				+ "-------------------\n"
				+ "- Added ability to select an item with ALT + <number> shortcut keys from filter field for first 10 rows in all tables.\n"
				+ "- Added ability to select search result rows with the standard keyboard keys when 'Containing text' field is focused on Search tab.\n\n"
				
				+ "Version 5.0\n"
				+ "-------------------\n"
				+ "- Added support for files.\n"
				+ "- Upgraded dependent libraries to be compatible with newer operating systems versions.\n\n"
				
				+ "Version 4.4\n"
				+ "-------------------\n"
				+ "- Maximum items count kept in clipboard history increased to 10000.\n"
				+ "- Some internal improvements.\n\n"
				
				+ "Version 4.3\n"
				+ "-------------------\n"
				+ "- Added more keyboard shortcuts for table rows selection when filter field is focused (Shift + Up/Down, Ctrl + Home/End keys).\n"
				+ "- Some internal improvements.\n\n"
				
				+ "Version 4.2\n"
				+ "-------------------\n"
				+ "- Maximum items count kept in clipboard history and groups increased to 3000.\n"
				+ "- Added ability to open Keepboard current working folder: Help -> About Keepboard -> Keepboard folder.\n"
				+ "- Added \"What's new\" dialog: Help -> What's new?\n\n"
				
				+ "Version 4.1\n"
				+ "-------------------\n"
				+ "- Added ability to specify maximum items count kept in clipboard history.\n\n"
				
				+ "Version 4.0\n"
				+ "-------------------\n"
				+ "- Added ability to specify preview area location (top, left, bottom, right): View -> Preview area location.\n"
				+ "- Main frame current size and preview dialogs size is saved across sessions.\n"
				+ "- Split pane divider current location is saved across sessions.\n"
				+ "- Added line wrapping ability for all multi-line text components.\n"
				+ "- Some other minor improvements.\n\n"

				+ "Version 3.3\n"
				+ "-------------------\n"
				+ "- Corrected bug that caused Keepboard not to save history across sessions when running with Java 7.\n"
				+ "- Added \"Main frame\" group of keyboard shortcut keys (currently for new, open and copy selected items).\n\n"

				+ "Version 3.2\n"
				+ "-------------------\n"
				+ "- Added standard cut-copy-paste context menu at all multi-line text components.\n"
				+ "- If there are no groups and they are not manually deleted, \"Favorites\" group is created automatically.\n\n"

				+ "Version 3.1\n"
				+ "-------------------\n"
				+ "- Added ability to remove Keepboard icon from system tray in order to save space.\n"
				+ "- If no item is selected in item tables, the first item is now taken by default.\n"
				+ "- Corrected bug that caused item preview area to be empty after Keepboard startup if clipboard was empty.\n\n"

				+ "Version 3.0\n"
				+ "-------------------\n"
				+ "- Added toolbar.\n"
				+ "- Added ability to reorder items.\n\n"

				+ "Version 2.3\n"
				+ "-------------------\n"
				+ "- Added more view customization.\n\n"

				+ "Version 2.2\n"
				+ "-------------------\n"
				+ "- Added search panel: Tools -> Search.\n"
				+ "- Corrected bug that allowed duplicates of large text items.\n\n"

				+ "Version 2.1\n"
				+ "-------------------\n"
				+ "- Added more auto-paste customization.\n\n"

				+ "Version 2.0\n"
				+ "-------------------\n"
				+ "- Added support for images.\n"
				+ "- Added preview item panel at the bottom of main frame.";
	}
}
