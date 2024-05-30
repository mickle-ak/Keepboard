package kb.gui.utils;

import kb.ClipboardManager;
import kb.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.net.URI;

public class HyperLinkPane extends JEditorPane {
	private static final long serialVersionUID = 1L;
	
	private String hyperLink;
	private Mode mode;
	
	public HyperLinkPane(String hyperLink, Mode mode) {
		super("text/html", createText(hyperLink, mode));
		this.hyperLink = hyperLink;
		this.mode = mode;
		
		init();
	}
	
	private static String createText(String hyperLink, Mode mode) {
		String link = mode == Mode.LINK ? hyperLink : "mailto:" + hyperLink;
		return "<div align=\"center\"><a href=\"" + link + "\">" + hyperLink + "</a></div>";
	}

	private void init() {
		setEditable(false);
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0)); // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6687960
		addHyperlinkListener(createHyperlinkListener());
	}

	private HyperlinkListener createHyperlinkListener() {
		return new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					hyperLinkActivated();
				}
			}
		};
	}

	private void hyperLinkActivated() {
		try {
			if (mode == Mode.MAIL) {
				if (Utils.isDesktopActionSupported(Desktop.Action.MAIL)) {
					Desktop.getDesktop().mail(new URI("mailto:" + hyperLink));
				} else {
					copyHyperLinkAndNotify(hyperLink, "Mail");
				}
			} else if (mode == Mode.LINK) {
				if (Utils.isDesktopActionSupported(Desktop.Action.BROWSE)) {
					Desktop.getDesktop().browse(new URI(hyperLink));
				} else {
					copyHyperLinkAndNotify(hyperLink, "URL");
				}
			}
		} catch (Exception e) {
			copyHyperLinkAndNotify(hyperLink, "Link");
		}
	}

	private void copyHyperLinkAndNotify(String hyperLink, String hyperLinkType) {
		ClipboardManager.getInstance().copyToClipboard(hyperLink);
		JOptionPane.showMessageDialog(this, hyperLinkType + " copied to clipboard.",
				hyperLinkType, JOptionPane.INFORMATION_MESSAGE);
	}

	public enum Mode {
		LINK, MAIL
	}
}
