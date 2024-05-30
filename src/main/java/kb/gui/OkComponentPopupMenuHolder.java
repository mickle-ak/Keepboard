package kb.gui;

import kb.PreferencesPersistor;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OkComponentPopupMenuHolder {
	
	public enum PasteMode {
		PASTE,
		PUT_IN_CLIPBOARD
	}
	
	private JPopupMenu popupMenu;
	private List<MyMenuItem> menuItems = new ArrayList<MyMenuItem>();
	
	private PasteMode pasteMode;
	
	public OkComponentPopupMenuHolder() {
		createPopupMenu();
		initState();
	}

	private void initState() {
		pasteMode = MyPreferences.INSTANCE.getPasteMode();
		
		for (MyMenuItem menuItem : menuItems) {
			if (menuItem.getPasteMode() == pasteMode) {
				menuItem.setSelected(true);
				break;
			}
		}
	}

	private void createPopupMenu() {
		popupMenu = new JPopupMenu();
		popupMenu.add(createMyMenuItem("Put to clipboard and paste if SHIFT not pressed", PasteMode.PASTE));
		popupMenu.add(createMyMenuItem("Always only put to clipboard", PasteMode.PUT_IN_CLIPBOARD));
	}
	
	private JMenuItem createMyMenuItem(String text, PasteMode pasteMode) {
		final MyMenuItem result = new MyMenuItem(pasteMode);
		result.setText(text);
		menuItems.add(result);
		
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectionPerformed(result);
			}
		});
		
		return result;
	}

	private void selectionPerformed(MyMenuItem menuItem) {
		pasteMode = menuItem.getPasteMode();
		
		// Clear selection on other menu items
		for (JCheckBoxMenuItem checkBoxMenuItem : menuItems) {
			checkBoxMenuItem.setSelected(checkBoxMenuItem == menuItem);
		}
		
		MyPreferences.INSTANCE.setPasteMode(pasteMode);
		new PreferencesPersistor().storeToDisk();
	}
	
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public PasteMode getPasteMode() {
		return pasteMode;
	}

	private static class MyMenuItem extends JCheckBoxMenuItem {
		private static final long serialVersionUID = 1L;
		
		private PasteMode pasteMode;
		
		public MyMenuItem(PasteMode pasteMode) {
			this.pasteMode = pasteMode;
		}

		public PasteMode getPasteMode() {
			return pasteMode;
		}
	}
}
