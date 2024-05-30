package kb.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ItemGroupModificationDialog extends MyInputDialog<String> {
	private static final long serialVersionUID = 1L;
	
	private static ItemGroupModificationDialog INSTANCE;
	
	private String name;
	private JPanel valuePanel;
	private JTextField textField;
	private JLabel label;
	
	private ItemGroupModificationDialog() {
		createComponents();
	}
	
	public static String showDialog(String name) {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.initComponents(name);
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}
	
	private void initComponents(String name) {
		this.name = name;
		label.setText(name == null ? "Group name: " : "Group new name: ");
		textField.setText(name != null ? name : "");
		setTitle(name == null ? "New saved items group" : "Rename saved items group");
		checkOkButtonEnabledState();
		textField.requestFocusInWindow();
	}
	
	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new ItemGroupModificationDialog();
		}
	}

	private void createComponents() {
		createValuePanel();
		addDialogComponents();
		setSize(400, 120);
	}
	
	private void createValuePanel() {
		valuePanel = new JPanel(new BorderLayout());
		valuePanel.add(createContentValuePanel(), BorderLayout.NORTH);
		addTextFieldActions();
	}
	
	private Component createContentValuePanel() {
		JPanel result = new JPanel(new GridBagLayout());
		addLabel(result);
		addTextField(result);
		return result;
	}

	private void addTextFieldActions() {
		textField.getDocument().addDocumentListener(createDocumentListener());
	}
	
	private DocumentListener createDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkOkButtonEnabledState();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkOkButtonEnabledState();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Not needed
			}
		};
	}

	private void checkOkButtonEnabledState() {
		String text = textField.getText().trim();
		getOkButton().setEnabled(!text.isEmpty() && (name == null || !name.equals(text))
				&& !text.toLowerCase().startsWith("<html>")); // Otherwise, group name would be rendered as html text in combo box
	}

	private void addLabel(JPanel panel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		panel.add(label = new JLabel());
	}

	private void addTextField(JPanel panel) {
		textField = new JTextField();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(textField, c);
	}

	@Override
	protected JPanel getValuePanel() {
		return valuePanel;
	}

	@Override
	protected String getValue() {
		return textField.getText().trim();
	}
}
