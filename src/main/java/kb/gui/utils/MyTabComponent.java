package kb.gui.utils;

import kb.gui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyTabComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton closeButton;
	private JLabel label;
	
	public MyTabComponent(String title, int mnemonic, Action closeAction) {
		setLayout(new GridBagLayout());
		setOpaque(false);
		createCloseButton(closeAction);
		createLabel(title, mnemonic);
		addComponents();
	}

	private void createLabel(String title, int mnemonic) {
		label = new JLabel(title + " ");
		label.setDisplayedMnemonic(mnemonic);
	}

	private void createCloseButton(Action action) {
		closeButton = new JButton(action);
		closeButton.setIcon(new XIcon(closeButton.getModel()));
		closeButton.addActionListener(createMyActionListener());
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusable(false);
		closeButton.setBorder(BorderFactory.createEtchedBorder());
		closeButton.setRolloverEnabled(true);
		closeButton.setBorderPainted(false);
		closeButton.addMouseListener(createMouseListener());
		GuiUtils.setSize(closeButton, new Dimension(13, 13));
	}

	private ActionListener createMyActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeButton.setBorderPainted(false);
			}
		};
	}

	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				closeButton.setBorderPainted(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				closeButton.setBorderPainted(false);
			}
		};
	}

	private void addComponents() {
		addLabel();
		addButton();
	}

	private void addLabel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		add(label);
	}

	private void addButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		add(closeButton);
	}

	public JButton getCloseButton() {
		return closeButton;
	}
}
