package kb.gui;

import kb.ImageClipboardItem;
import kb.utils.Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

public class ImageItemDialog extends MyInputDialog<Boolean> {
	private static final long serialVersionUID = 1L;
	
	private static ImageItemDialog INSTANCE;
	
	private JPanel valuePanel;
	
	private JLabel lblName;
	private JTextField txtName;
	private JLabel imageLabel;
	private boolean editName;
	
	private ImageClipboardItem item;
	
	private ImageItemDialog() {
		createAndAddComponents();
	}
	
	public static Boolean showDialog(ImageClipboardItem item, boolean editName) {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.setItem(item, editName);
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}

	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new ImageItemDialog();
		}
	}

	private void createAndAddComponents() {
		valuePanel = createValuePanel();
		addDialogComponents();
		addActions();
		setSize(600, 400);
	}
	
	private JPanel createValuePanel() {
		JPanel result = new JPanel(new GridBagLayout());
		createAndAddNameField(result);
		createAndAddItemField(result);
		return result;
	}
	
	private void createAndAddNameField(JPanel result) {
		txtName = new JTextField();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		result.add(lblName = createTxtNameLabel(), c);
		
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
		result.add(imageLabel = createImageLabel(), c);
	}
	
	private JLabel createImageLabel() {
		JLabel result = new JLabel();
		result.setHorizontalAlignment(SwingConstants.CENTER);
		return result;
	}
	
	private void setItem(ImageClipboardItem item, boolean editName) {
		this.item = item;
		this.editName = editName;
		setTitle(editName ? "Edit image item name" : "Image preview");
		initComponents();
		checkButtonOkEnabledState();
	}

	private void initComponents() {
		txtName.setVisible(editName);
		lblName.setVisible(editName);
		getOkButton().setVisible(editName);
		getCancelButton().setText(editName ? "Cancel" : "Close");
		txtName.setText(editName ? item.getName() : "");
		imageLabel.setIcon(new ImageIcon(Utils.scale(item.getContent().getImage(), 580, 280)));
		txtName.requestFocusInWindow();
	}

	private void checkButtonOkEnabledState() {
		getOkButton().setEnabled(editName && !txtName.getText().trim().isEmpty()
				&& !item.getName().equals(txtName.getText()));
	}
	
	private void addActions() {
		txtName.getDocument().addDocumentListener(createDocumentListener());
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

	@Override
	protected Boolean getValue() {
		// Called if OK button is enabled and pressed
		item.setName(txtName.getText());
		return true;
	}

	@Override
	protected JPanel getValuePanel() {
		return valuePanel;
	}
}
