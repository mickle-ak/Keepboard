package kb.gui.utils;

import kb.gui.MyMouseListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.MouseEvent;

public class TextComponentContextMenuHandler {
	
	public static JPopupMenu addContextMenu(final JTextComponent textComponent) {
		final TextComponentContextMenu result = new TextComponentContextMenu(textComponent);
		textComponent.addMouseListener(new MyMouseListener() {
			@Override
			public void contextMenuTriggered(MouseEvent e) {
				textComponent.requestFocusInWindow();
				result.show(textComponent, e.getX(), e.getY());
			}
		});
		return result;
	}
}