package kb.gui;

import kb.ClipboardItem;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;


public class NamedItemEditDialog extends MyInputDialog<ClipboardItem> {
	private static final long serialVersionUID = 1L;
	
	private static NamedItemEditDialog INSTANCE;
	
	private JPanel valuePanel;
	
	private JTextField txtName;
	private TextAreaComponent textAreaComponent;
	
	private ClipboardItem itemToEdit;
	private String itemToEditContent;

	private NamedItemEditDialog() {
		createAndAddComponents();
		DialogClosingCommandsExecutor.INSTANCE.addCommand(createDialogClosingCommand());
	}
	
	private DialogClosingCommand createDialogClosingCommand() {
		return new DialogClosingCommand() {
			@Override
			public void execute() {
				executeLineWrapCommand();
				executeDialogResizeCommand();
			}
		};
	}
	
	private void executeLineWrapCommand() {
		boolean oldValue = MyPreferences.INSTANCE.isNamedItemsPreviewDialogLineWrap();
		boolean newValue = textAreaComponent.isLineWrap();
		if (oldValue != newValue) {
			MyPreferences.INSTANCE.setNamedItemsPreviewDialogLineWrap(newValue);
			new PreferencesPersistor().storeToDisk();
		}
	}

	private void executeDialogResizeCommand() {
		Size oldSize = MyPreferences.INSTANCE.getNamedItemsPreviewDialogSize();
		Size newSize = new Size(getWidth(), getHeight());
		if (!oldSize.equals(newSize)) {
			MyPreferences.INSTANCE.setNamedItemsPreviewDialogSize(newSize);
			new PreferencesPersistor().storeToDisk();
		}
	}
	
	public static ClipboardItem showDialog(ClipboardItem itemToEdit) {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.setItemToEdit(itemToEdit);
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}

	private void setItemToEdit(ClipboardItem itemToEdit) {
		this.itemToEdit = itemToEdit;
		this.itemToEditContent = itemToEdit != null ? ((TextClipboardContent) itemToEdit.getContent()).getText() : null;
		setTitle(itemToEdit == null ? "New named item" : "Edit named item");
		initComponents();
		checkButtonOkEnabledState();
	}

	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new NamedItemEditDialog();
		}
	}

	private void createAndAddComponents() {
		valuePanel = createValuePanel();
		addDialogComponents();
		addActions();
		Size size = MyPreferences.INSTANCE.getNamedItemsPreviewDialogSize();
		setSize(size.getWidth(), size.getHeight());
	}
	
	private void addActions() {
		txtName.getDocument().addDocumentListener(createDocumentListener());
		textAreaComponent.getTextArea().getDocument().addDocumentListener(createDocumentListener());
		txtName.addFocusListener(createTxtNameFocusListener());
	}
	
	private FocusListener createTxtNameFocusListener() {
		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				txtName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtName.select(0, 0);
			}
		};
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
		getOkButton().setEnabled(!isNameEmpty() && !isItemContentEmpty() && isAnythingChanged());
	}

	private boolean isNameEmpty() {
		return txtName.getText().trim().isEmpty();
	}

	private boolean isItemContentEmpty() {
		return textAreaComponent.getText().trim().isEmpty();
	}

	private boolean isAnythingChanged() {
		return itemToEdit == null || !itemToEditContent.equals(textAreaComponent.getText()) 
			|| !itemToEdit.getName().equals(txtName.getText());
	}

	private JPanel createValuePanel() {
		JPanel result = new JPanel(new GridBagLayout());
		createAndAddNameField(result);
		createAndAddItemField(result);
		textAreaComponent.setLineWrap(MyPreferences.INSTANCE.isNamedItemsPreviewDialogLineWrap());
		return result;
	}

	private void initComponents() {
		if (itemToEdit != null)	{
			txtName.setText(itemToEdit.getName());
			textAreaComponent.setText(itemToEditContent);
			adjustTxaItemScrollbars();
		} else {
			txtName.setText("");
			textAreaComponent.setText("");
		}
		
		txtName.requestFocusInWindow();
	}

	private void adjustTxaItemScrollbars() {
		textAreaComponent.getTextArea().setCaretPosition(0);
	}

	private void createAndAddNameField(JPanel result) {
		txtName = new JTextField();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		result.add(createTxtNameLabel(), c);
		
		c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		result.add(txtName, c);
	}
	
	private JLabel createTxtNameLabel() {
		JLabel result = new JLabel("Name: ");
		result.setLabelFor(txtName);
		result.setDisplayedMnemonic(KeyEvent.VK_E);
		return result;
	}

	private void createAndAddItemField(JPanel result) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.insets = new Insets(7, 0, 0, 0);
		result.add((textAreaComponent = new TextAreaComponent()).getComponent(), c);
	}
	
	@Override
	protected ClipboardItem getValue() {
		return createValue();
	}

	private ClipboardItem createValue() {
		ClipboardItem result = new TextClipboardItem(textAreaComponent.getText());
		result.setName(txtName.getText().trim());
		return result;
	}

	@Override
	protected JPanel getValuePanel() {
		return valuePanel;
	}
}
