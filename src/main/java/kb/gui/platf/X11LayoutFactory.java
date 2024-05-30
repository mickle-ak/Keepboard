package kb.gui.platf;

import kb.gui.ButtonsPanelLayout;
import kb.gui.MainFrameButtonsPanel;
import kb.gui.OKCancelLayout;

import javax.swing.*;


public class X11LayoutFactory extends PlatformDependentLayoutFactory {

	@Override
	public ButtonsPanelLayout getButtonsPanelLayout(MainFrameButtonsPanel buttonsPanel) {
		return new X11ButtonPanelLayout(buttonsPanel);
	}

	@Override
	public OKCancelLayout getOKCancelLayout(JPanel panel, JButton okButton, JButton cancelButton) {
		return new X11OKCancelLayout(panel, okButton, cancelButton);
	}
}
