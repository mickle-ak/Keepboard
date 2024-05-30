package kb.gui.platf;

import kb.gui.ButtonsPanelLayout;
import kb.gui.MainFrameButtonsPanel;

import java.awt.*;


class X11ButtonPanelLayout implements ButtonsPanelLayout {
	private MainFrameButtonsPanel buttonsPanel;
	
	public X11ButtonPanelLayout(MainFrameButtonsPanel buttonsPanel) {
		this.buttonsPanel = buttonsPanel;
	}
	
	@Override
	public void layoutButtons() {
		buttonsPanel.setLayout(new GridBagLayout());
		addCancelButton();
		addOkComponent();
	}

	private void addCancelButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 1;
		buttonsPanel.add(buttonsPanel.getBtnCancel(), c);
	}

	private void addOkComponent() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.insets = new Insets(0, 7, 0, 0);
		buttonsPanel.add(buttonsPanel.getOkComponent(), c);
	}
}
