package kb.gui;

import kb.PreferencesPersistor;
import kb.TextClipboardContent;
import kb.TextClipboardItem;
import kb.gui.prefs.MyPreferences;
import kb.gui.prefs.Size;
import kb.gui.utils.TextAreaComponent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ClipboardHistoryItemEditDialog extends MyInputDialog<TextClipboardItem> {
	private static final long serialVersionUID = 1L;
	
	private static ClipboardHistoryItemEditDialog INSTANCE;
	
	private JPanel valuePanel;
	
	private TextAreaComponent textAreaComponent;
	
	private String itemToEditContent;
	
	private ClipboardHistoryItemEditDialog() {
		createAndAddComponents();
		DialogClosingCommandsExecutor.INSTANCE.addCommand(createDialogClosingCommand());
	}
	
	private DialogClosingCommand createDialogClosingCommand() {
		return new DialogClosingCommand() {
			@Override
			public void execute() {
				executeDialogClosingCommand();
			}
		};
	}

	private void executeDialogClosingCommand() {
		executeLineWrapCommand();
		executeDialogResizeCommand();
	}

	private void executeLineWrapCommand() {
		boolean oldValue = MyPreferences.INSTANCE.isClipboardHistoryPreviewDialogLineWrap();
		boolean newValue = textAreaComponent.isLineWrap();
		if (oldValue != newValue) {
			MyPreferences.INSTANCE.setClipboardHistoryPreviewDialogLineWrap(newValue);
			new PreferencesPersistor().storeToDisk();
		}
	}

	private void executeDialogResizeCommand() {
		Size oldSize = MyPreferences.INSTANCE.getClipboardHistoryPreviewDialogSize();
		Size newSize = new Size(getWidth(), getHeight());
		if (!oldSize.equals(newSize)) {
			MyPreferences.INSTANCE.setClipboardHistoryPreviewDialogSize(newSize);
			new PreferencesPersistor().storeToDisk();
		}
	}

	public static TextClipboardItem showDialog(TextClipboardItem itemToEdit) {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.setItemToEdit(itemToEdit);
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}

	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new ClipboardHistoryItemEditDialog();
		}
	}
	
	private void setItemToEdit(TextClipboardItem itemToEdit) {
		this.itemToEditContent = itemToEdit != null ? ((TextClipboardContent) itemToEdit.getContent()).getText() : null;
		setTitle(itemToEdit == null ? "New item" : "Edit item");
		initComponents();
	}

	private void createAndAddComponents() {
		valuePanel = createValuePanel();
		addActions();
		addDialogComponents();
		Size size = MyPreferences.INSTANCE.getClipboardHistoryPreviewDialogSize();
		setSize(size.getWidth(), size.getHeight());
	}
	
	private void addActions() {
		textAreaComponent.getTextArea().getDocument().addDocumentListener(createDocumentListener());
	}
	
	private DocumentListener createDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkButtonOkEnabledState();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkButtonOkEnabledState();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Not needed
			}
		};
	}

	private void checkButtonOkEnabledState() {
		getOkButton().setEnabled(!isItemContentEmpty() && isAnythingChanged());
	}

	private boolean isItemContentEmpty() {
		return textAreaComponent.getText().trim().isEmpty();
	}

	private boolean isAnythingChanged() {
		return itemToEditContent == null || !itemToEditContent.equals(textAreaComponent.getText());
	}

	private JPanel createValuePanel() {
		JPanel result = new JPanel(new GridBagLayout());
		createAndAddItemField(result);
		textAreaComponent.setLineWrap(MyPreferences.INSTANCE.isClipboardHistoryPreviewDialogLineWrap());
		return result;
	}

	private void initComponents() {
		adjustTxaItem();
		checkButtonOkEnabledState();
		textAreaComponent.getTextArea().requestFocusInWindow();
	}

	private void adjustTxaItem() {
		if (itemToEditContent != null)	{
			textAreaComponent.setText(itemToEditContent);
			adjustTxaItemScrollbars();
		} else {
			textAreaComponent.setText("");
		}
	}

	private void adjustTxaItemScrollbars() {
		textAreaComponent.getTextArea().setCaretPosition(0);
	}

	private void createAndAddItemField(JPanel result) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		result.add((textAreaComponent = new TextAreaComponent()).getComponent(), c);
	}
	
	@Override
	protected TextClipboardItem getValue() {
		return createValue();
	}

	private TextClipboardItem createValue() {
		return new TextClipboardItem(textAreaComponent.getText());
	}

	@Override
	protected JPanel getValuePanel() {
		return valuePanel;
	}
}
