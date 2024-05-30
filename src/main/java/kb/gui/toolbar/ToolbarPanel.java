package kb.gui.toolbar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolbarPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private List<List<ToolbarButton>> groups = new ArrayList<List<ToolbarButton>>();
	
	public ToolbarPanel() {
		setLayout(new GridBagLayout());
	}
	
	public void addToolbarButtonsGroup(List<ToolbarButton> buttons) {
		groups.add(buttons);
	}
	
	public void build() {
		int x = 0;
		for (List<ToolbarButton> group : groups) {
			if (x != 0) {
				addSeparator(x++);
			}
			addToolbarButtonGroup(group, x);
			x += group.size();
		}
		addEmptyLabel(x);
	}
	
	private void addEmptyLabel(int x) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(), c);
	}

	
	private void addSeparator(int x) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 0;
		c.insets = new Insets(2, 0, 2, 0);
		c.fill = GridBagConstraints.VERTICAL;
		add(new JSeparator(SwingConstants.VERTICAL), c);
	}

	private void addToolbarButtonGroup(List<ToolbarButton> buttons, int x) {
		for (ToolbarButton button : buttons) {
			addToolbarButton(button, x++);
		}
	}

	private void addToolbarButton(ToolbarButton button, int x) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(button, c);
	}
}
