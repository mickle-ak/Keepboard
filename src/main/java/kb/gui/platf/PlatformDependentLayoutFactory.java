package kb.gui.platf;

import com.sun.jna.Platform;
import kb.gui.ButtonsPanelLayout;
import kb.gui.MainFrameButtonsPanel;
import kb.gui.OKCancelLayout;

import javax.swing.*;

public abstract class PlatformDependentLayoutFactory {
	
	public static final PlatformDependentLayoutFactory getInstance() {
		if (Platform.isX11()) {
			return new X11LayoutFactory();
		} else {
			return new NonX11LayoutFactory();
		}
	}
	
	public abstract ButtonsPanelLayout getButtonsPanelLayout(MainFrameButtonsPanel buttonsPanel);
	
	public abstract OKCancelLayout getOKCancelLayout(JPanel panel, JButton okButton, JButton cancelButton); 
}
