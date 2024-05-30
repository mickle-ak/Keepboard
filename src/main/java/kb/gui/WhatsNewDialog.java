package kb.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WhatsNewDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JButton btnClose;
	
	public WhatsNewDialog() {
		super(MainFrame.getInstance(), "What's new?", true);
		addComponents();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addActions();
		setSize(600, 400);
		setLocationRelativeTo(MainFrame.getInstance());
		setVisible(true);
	}

	private void addActions() {
		registerEscapeKey();
		addComponentListener();
	}
	
	private void addComponentListener() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				btnClose.requestFocusInWindow();
			}
		});
	}
	
	private void registerEscapeKey() {
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), "myEscape");
		
		getRootPane().getActionMap().put("myEscape", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				dispose();
			}
		});
	}
	
	private void addComponents() {
		setLayout(new GridBagLayout());
		addContentPanel();
		addButtonsPanel();
	}

	private void addContentPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		add(createContentTextArea(), c);
	}

	private Component createContentTextArea() {
		JTextArea result = new JTextArea(WhatsNewContent.getContent());
		result.setWrapStyleWord(true);
		result.setLineWrap(true);
		return new JScrollPane(result);
	}

	private void addButtonsPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.insets = new Insets(0, 0, 5, 5);
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		add(createButtonClose(), c);
	}

	private JButton createButtonClose() {
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		GuiUtils.setSize(btnClose, new Dimension(100, 30));
		return btnClose;
	}
}
