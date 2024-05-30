package kb.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyMouseListener extends MouseAdapter {
	private boolean mousePressedHandlesPopupTriggerEvent = false;
	private boolean mouseReleasedHandlesPopupTriggerEvent = false;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger() && !mouseReleasedHandlesPopupTriggerEvent) {
			mousePressedHandlesPopupTriggerEvent = true;
			contextMenuTriggered(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			mouseDoubleClicked(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && !mousePressedHandlesPopupTriggerEvent) {
			mouseReleasedHandlesPopupTriggerEvent = true;
			contextMenuTriggered(e);
		}
	}
	
	public void mouseDoubleClicked(MouseEvent e) {
		// Override if needed
	}
	
	public void contextMenuTriggered(MouseEvent e) {
		// Override if needed
	}
}