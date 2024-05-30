package kb.gui.utils;

import kb.ClipboardManager;
import kb.clipboard.ClipboardAcessor;
import kb.gui.PopupMenuUtils;
import kb.utils.PopupMenuAdapter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TextComponentContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	private JTextComponent textComponent;
	
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem selectAllMenuItem;
	
	public TextComponentContextMenu(JTextComponent textComponent) {
		this.textComponent = textComponent;
		createMenusItems();
		addMenuItems();
		addMenuVisibilityListener();
		PopupMenuUtils.addGlobalPopupMenuListener(this);
	}

	private void addMenuVisibilityListener() {
		this.addPopupMenuListener(new PopupMenuAdapter() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
				setMenuItemsEnableState();
			}
		});
	}
	
	private void setMenuItemsEnableState() {
		cutMenuItem.setEnabled(textComponent.isEditable() && !isEmpty(textComponent.getSelectedText()));
		copyMenuItem.setEnabled(!isEmpty(textComponent.getSelectedText()));
		pasteMenuItem.setEnabled(textComponent.isEditable() && !isEmpty(ClipboardAcessor.getClipboardText()));
		selectAllMenuItem.setEnabled(!isEmpty(textComponent.getText()));
	}

	private void createMenusItems() {
		cutMenuItem = createMenuItem("Cut", createCutActionListener());
		copyMenuItem = createMenuItem("Copy", createCopyActionListener());
		pasteMenuItem = createMenuItem("Paste", createPasteActionListener());
		selectAllMenuItem = createMenuItem("Select All", createSelectAllActionListener());
	}
	
	private ActionListener createCopyActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copySelectedText();
			}
		};
	}

	private ActionListener createPasteActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		};
	}

	private ActionListener createSelectAllActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		};
	}

	private ActionListener createCutActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cutSelectedText();
			}
		};
	}

	private void cutSelectedText() {
		String selectedText = textComponent.getSelectedText();
		if (!isEmpty(selectedText)) {
			copyToClipboard(selectedText);
			textComponent.replaceSelection("");
		}
	}
	
	private void copySelectedText() {
		String selectedText = textComponent.getSelectedText();
		if (!isEmpty(selectedText)) {
			copyToClipboard(selectedText);
		}
	}
	
	private void copyToClipboard(String text) {
		ClipboardManager.getInstance().copyToClipboard(text);
	}
	
	private void paste() {
		String text = ClipboardAcessor.getClipboardText();
		if (!isEmpty(text)) {
			textComponent.replaceSelection(text);
		}
	}
	
	private void selectAll() {
		textComponent.selectAll();
	}
	
	private boolean isEmpty(String aString) {
		return aString == null || aString.isEmpty();
	}

	private JMenuItem createMenuItem(String text, ActionListener actionListener) {
		JMenuItem result = new JMenuItem(text);
		result.addActionListener(actionListener);
		return result;
	}

	private void addMenuItems() {
		add(cutMenuItem);
		add(copyMenuItem);
		add(pasteMenuItem);
		addSeparator();
		add(selectAllMenuItem);
	}
}
