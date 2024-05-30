package kb.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class TextAreaComponent {
	private JPanel panel;
	private JTextArea textArea;
	private JCheckBox chkLineWrap;
	private JCheckBoxMenuItem miLineWrap;
	
	public TextAreaComponent() {
		createComponents();
		addComponents();
		addActions();
	}

	private void addActions() {
		chkLineWrap.addActionListener(new LineWrapChangeListener(miLineWrap));
		miLineWrap.addActionListener(new LineWrapChangeListener(chkLineWrap));
	}

	private void createComponents() {
		panel = new JPanel(new GridBagLayout());
		miLineWrap = new JCheckBoxMenuItem("Line wrap");
		textArea = createTextArea();
		chkLineWrap = createChkLineWrap();
	}

	private JTextArea createTextArea() {
		JTextArea result = new JTextArea();
		result.setTabSize(2);
		addContextMenu(result);
		return result;
	}

	private void addContextMenu(JTextArea result) {
		JPopupMenu popupMenu = TextComponentContextMenuHandler.addContextMenu(result);
		popupMenu.addSeparator();
		popupMenu.add(miLineWrap);
	}

	private JCheckBox createChkLineWrap() {
		JCheckBox result = new JCheckBox("Line wrap");
		result.setMnemonic(KeyEvent.VK_L);
		return result;
	}

	private void addComponents() {
		addTextArea();
		addChkLineWrap();
	}
	
	private void addTextArea() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new JScrollPane(textArea), c);
	}
	
	private void addChkLineWrap() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(chkLineWrap, c);
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}
	
	public Component getComponent() {
		return panel;
	}
	
	public void setLineWrapCheckBoxVisible(boolean visible) {
		chkLineWrap.setVisible(visible);
	}
	
	public void setLineWrap(boolean lineWrap) {
		chkLineWrap.setSelected(lineWrap);
		miLineWrap.setSelected(lineWrap);
		setTextAreaLineWrap(lineWrap);
	}

	private void setTextAreaLineWrap(boolean lineWrap) {
		textArea.setLineWrap(lineWrap);
		textArea.setWrapStyleWord(lineWrap);
	}
	
	public boolean isLineWrap() {
		return textArea.getLineWrap();
	}
	
	public String getText() {
		return textArea.getText();
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}
	
	private class LineWrapChangeListener implements ActionListener {
		
		private AbstractButton pairedButton;
		
		public LineWrapChangeListener(AbstractButton pairButton) {
			this.pairedButton = pairButton;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selected = ((AbstractButton) e.getSource()).isSelected();
			setLineWrap(selected);
			pairedButton.setSelected(selected);
		}
	}
}
