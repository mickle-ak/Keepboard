package kb.gui.toolbar;

import kb.gui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolbarButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private final Dimension size = new Dimension(25, 25);
	
	public ToolbarButton(Icon icon, Action action, String toolTipText) {
		super(action);
		init();
		setToolTipText(toolTipText);
		setIcon(icon);
		GuiUtils.setSize(this, size);
	}

	private void init() {
		setFocusable(false);
		setContentAreaFilled(false);
		addMouseListener(createMouseListener());
	}
	
	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setContentAreaFilled(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setContentAreaFilled(false);
			}
		};
	}
}
