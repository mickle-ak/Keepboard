package kb.gui.listeners;

import kb.TextClipboardContent;
import kb.clipboard.ClipboardAcessor;
import kb.texts.Texts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmptyClipboardActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		ClipboardAcessor.writeToClipboard(new TextClipboardContent(Texts.CLIPBOARD_CONTENT_PURGED));
	}
}