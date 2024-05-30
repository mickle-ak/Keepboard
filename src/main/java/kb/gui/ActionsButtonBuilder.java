package kb.gui;

import kb.utils.PopupMenuAdapter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ActionsButtonBuilder {
	private JPopupMenu actionsMenu;
	private JToggleButton actionsButton;
	private int mnemonic = -1;
	private String text;
	private Icon icon;
	private Dimension size;
	private boolean onTop = false;
	
	public ActionsButtonBuilder(JPopupMenu actionsMenu) {
		this.actionsMenu = actionsMenu;
		addActionsMenuListener();
		PopupMenuUtils.addGlobalPopupMenuListener(actionsMenu);
	}
	
	private void addActionsMenuListener() {
		actionsMenu.addPopupMenuListener(new PopupMenuAdapter() {
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				actionsButton.setSelected(false);
			}
		});
	}

	public AbstractButton build() {
		actionsButton = new JToggleButton();
		
		actionsButton.addActionListener(createActionsButtonActionsListener());
		
		if (mnemonic >= 0) {
			actionsButton.setMnemonic(mnemonic);
		}
		
		if (icon != null) {
			actionsButton.setIcon(icon);
		}
		
		if (text != null) {
			actionsButton.setText(text);
		}
		
		if (size != null) {
			GuiUtils.setSize(actionsButton, size);
		}
		
		return actionsButton;
	}
	
	public ActionsButtonBuilder setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
		return this;
	}
	
	private ActionListener createActionsButtonActionsListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (actionsButton.isSelected()) {
					displayActionsMenu();
				} else {
					actionsMenu.setVisible(false);
				}
			}
		};
	}

	private void displayActionsMenu() {
		int x = actionsButton.getWidth() - actionsMenu.getPreferredSize().width;
		int y = onTop ? 0 : (int) actionsButton.getLocation().getY();
		
		if (onTop) {
			y -= actionsMenu.getPreferredSize().height;
		} else {
			y += actionsButton.getHeight();
		}
		
		actionsMenu.show(actionsButton, x, y);
	}
	
	public ActionsButtonBuilder setText(String title) {
		this.text = title;
		return this;
	}
	
	public ActionsButtonBuilder setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}
	
	public ActionsButtonBuilder setSize(Dimension size) {
		this.size = size;
		return this;
	}
	
	public ActionsButtonBuilder setOnTop() {
		this.onTop = true;
		return this;
	}
}
