package kb.gui;

import kb.gui.utils.TriangleIcon;

import javax.swing.*;
import java.awt.*;

public class ArrowComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private AbstractButton arrow;
	
	public ArrowComponent(Component component, JPopupMenu arrowMenu) {
		this.setLayout(new GridBagLayout());
		addComponent(component);
		addArrow(arrowMenu);
	}

	private void addComponent(Component component) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		add(component, c);
	}

	private void addArrow(JPopupMenu arrowMenu) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.insets = new Insets(0, 1, 0, 0);
		add(arrow = createArrowButton(arrowMenu), c);
	}

	private AbstractButton createArrowButton(JPopupMenu arrowMenu) {
		AbstractButton result = new ActionsButtonBuilder(arrowMenu)
			.setIcon(new TriangleIcon())
			.setOnTop()
			.build();
		
		
		return result;
	}
	
	public AbstractButton getArrow() {
		return arrow;
	}
}
