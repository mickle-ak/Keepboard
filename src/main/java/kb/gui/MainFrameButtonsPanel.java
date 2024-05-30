package kb.gui;

import kb.gui.platf.PlatformDependentLayoutFactory;

import javax.swing.*;
import java.awt.*;

public class MainFrameButtonsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton btnOk;
	private JButton btnCancel;
	private ArrowComponent okComponent;
	
	public MainFrameButtonsPanel(JPopupMenu okComponentPopupMenu) {
		createComponents(okComponentPopupMenu);
		addComponents();
	}

	private void createComponents(JPopupMenu okComponentPopupMenu) {
		btnOk = new JButton("OK");
		okComponent = new ArrowComponent(btnOk, okComponentPopupMenu);
		btnCancel = new JButton("Cancel");
		
		GuiUtils.setSize(btnOk, new Dimension(100, 30));
		GuiUtils.setSize(btnCancel, new Dimension(100, 30));
		GuiUtils.setSize(okComponent.getArrow(), new Dimension(30, 30));
	}

	private void addComponents() {
		PlatformDependentLayoutFactory.getInstance().getButtonsPanelLayout(this).layoutButtons();
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}
	
	public ArrowComponent getOkComponent() {
		return okComponent;
	}
}
