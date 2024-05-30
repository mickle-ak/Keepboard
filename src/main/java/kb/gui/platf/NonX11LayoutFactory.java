package kb.gui.platf;

import kb.gui.ButtonsPanelLayout;
import kb.gui.MainFrameButtonsPanel;
import kb.gui.OKCancelLayout;

import javax.swing.*;

public class NonX11LayoutFactory extends PlatformDependentLayoutFactory {

	@Override
	public ButtonsPanelLayout getButtonsPanelLayout(MainFrameButtonsPanel buttonsPanel) {
		return new NonX11ButtonPanelLayout(buttonsPanel);
	}

	@Override
	public OKCancelLayout getOKCancelLayout(JPanel panel, JButton okButton, JButton cancelButton) {
		return new NonX11OKCancelLayout(panel, okButton, cancelButton);
	}
}
