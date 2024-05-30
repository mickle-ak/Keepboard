package kb.gui;

import kb.utils.PopupMenuAdapter;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;

public final class PopupMenuUtils {
	
	public static void addGlobalPopupMenuListener(JPopupMenu popupMenu) {
		popupMenu.addPopupMenuListener(new EscKeyHandler());
	}
	
	private static class EscKeyHandler extends PopupMenuAdapter {

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
			MainFrame.getInstance().registerEscapeKey();
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
			MainFrame.getInstance().unregisterEscapeKey();
		}
	}
}
