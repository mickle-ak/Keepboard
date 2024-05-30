package kb.gui.platf;

import kb.gui.OKCancelLayout;

import javax.swing.*;
import java.awt.*;


class X11OKCancelLayout implements OKCancelLayout {
	
	private JPanel panel;
	private JButton okButton;
	private JButton cancelButton;
	
	public X11OKCancelLayout(JPanel panel, JButton okButton, JButton cancelButton) {
		this.panel = panel;
		this.okButton = okButton;
		this.cancelButton = cancelButton;
	}

	@Override
	public void layoutButtons() {
		panel.setLayout(new GridBagLayout());
		addCancelButton();
		addOkButton();
	}

	private void addCancelButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		panel.add(cancelButton, c);
	}
	
	private void addOkButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 0);
		panel.add(okButton, c);
	}

}
