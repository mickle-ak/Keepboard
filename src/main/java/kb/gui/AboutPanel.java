package kb.gui;

import kb.ClipboardManager;
import kb.PreferencesPersistor;
import kb.gui.prefs.MyPreferences;
import kb.gui.utils.HyperLinkPane;
import kb.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.net.URI;

public class AboutPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String LICENSE_URL = "http://www.gnu.org/licenses/";
	
	private static ImageIcon logo;
	private static final int logoHeight = 100;

	public enum FocusedTab {
		LICENSE, DONATE;
	}
	
	public AboutPanel(FocusedTab focusedTab) {
		setLayout(new GridBagLayout());
		addLogo();
		addInfo();
		addTabs(focusedTab);
	}
	
	private static ImageIcon getLogo() {
		if (logo == null) {
			logo = createLogo();
		}
		return logo;
	}
	
	private static ImageIcon createLogo() {
		Image image = new ImageIcon(Utils.getImage("myIcon.JPG")).getImage();
		double logoScaleRatio = ((double) logoHeight) / image.getHeight(null);
		Image scaled = image.getScaledInstance((int) (image.getWidth(null) * logoScaleRatio),
				logoHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
	
	private void addLogo() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(getImageLabel(), c);
	}

	private JLabel getImageLabel() {
		ImageIcon logo = getLogo();
		JLabel imageLabel = new JLabel(logo);
		GuiUtils.setSize(imageLabel, new Dimension(logo.getImage().getWidth(null), logo.getImage().getHeight(null)));
		return imageLabel;
	}

	private void addInfo() {
		JPanel panel = new JPanel(new GridLayout(5, 1));
		
		JLabel label = new JLabel("Keepboard", SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize() + 4));
		panel.add(label);
		
		panel.add(new JLabel("Version 5.7", SwingConstants.CENTER));
		panel.add(new JLabel("Copyright 2011-2022 Dragan Bozanovic", SwingConstants.CENTER));
		panel.add(new HyperLinkPane("bozanovicdr@gmail.com", HyperLinkPane.Mode.MAIL));
		panel.add(new HyperLinkPane("http://sourceforge.net/projects/keepboard/", HyperLinkPane.Mode.LINK));
		
		GuiUtils.setSize(panel, new Dimension(1, 100));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(panel, c);
	}

	private void addTabs(FocusedTab focusedTab) {
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		pane.addTab("License", new JScrollPane(createLicenseEditorPane()));
		pane.addTab("Donate", new JScrollPane(createDonateEditorPane()));
		if (focusedTab == FocusedTab.DONATE) {
			pane.setSelectedIndex(1);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 0, 0, 0);
		add(pane, c);
	}
	
	private JEditorPane createLicenseEditorPane() {
		JEditorPane result = new JEditorPane("text/html", getLicenceShortText());
		result.setEditable(false);
		result.addHyperlinkListener(createLicenseHyperlinkListener());
		result.setCaretPosition(0);
		return result;
	}

	private HyperlinkListener createLicenseHyperlinkListener() {
		return new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					if (Utils.isDesktopActionSupported(Desktop.Action.BROWSE)) {
						try {
							Desktop.getDesktop().browse(new URI(LICENSE_URL));
						} catch (Throwable t) {
							copyLinkAndNotify(LICENSE_URL, "License URL");
						}
					} else {
						copyLinkAndNotify(LICENSE_URL, "License URL");
					}
				}
			}
		};
	}

	private String getLicenceShortText() {
		return "<html>Keepboard is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by "
			 + "the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br /><br />"
			 + "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of "
			 + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.<br /><br />"
			 + "You should have received a copy of the GNU General Public License along with this program.  If not, see "
			 + "<a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a>.</html>";
	}

	private JEditorPane createDonateEditorPane() {
		JEditorPane result = new JEditorPane("text/html", getDonateText());
		result.setEditable(false);
		result.addHyperlinkListener(createDonateHyperlinkListener());
		result.setCaretPosition(0);
		return result;
	}

	private HyperlinkListener createDonateHyperlinkListener() {
		return new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					MyPreferences.INSTANCE.setDonateReminderOff(true);
					new PreferencesPersistor().storeToDisk();
					if (e.getDescription().contains("paypal")) {
						if (Utils.isDesktopActionSupported(Desktop.Action.BROWSE)) {
							try {
								Desktop.getDesktop().browse(new URI((e.getDescription())));
							} catch (Throwable t) {
								copyLinkAndNotify(e.getDescription(), "PayPal donation URL");
							}
						} else {
							copyLinkAndNotify(e.getDescription(), "PayPal donation URL");
						}
					} else if (e.getDescription().contains("monero")){
						copyLinkAndNotify(
								"479Lbeiedn564qERdRCBseM7gEa8yTcHZBFq1QNBrWL1MuP2bhxg1Xg44P8saquaqqC1ZYPvjbGeu6PBmxjLg662CFrVxCg",
								"XMR address");
					}
				}
			}
		};
	}

	private void copyLinkAndNotify(String link, String linkType) {
		ClipboardManager.getInstance().copyToClipboard(link);
		JOptionPane.showMessageDialog(this, linkType + " copied to clipboard.",
				linkType, JOptionPane.INFORMATION_MESSAGE);
	}

	private String getDonateText() {
		String template = "<html>" +
				"Keepboard is 100% money-free and ad-free and will always be so!<br /><br />" +
				"If you are happy using it, please consider making a donation to help with new features and maintenance. " +
				"Every little bit helps! Keepboard supports the following donation methods:<br /><br />" +
				"- <a href=\"https://paypal.me/DraganBozanovic\">PayPal</a> ({payPalDonationPage})<br />" +
				"- <a href=\"monero\">Monero</a> (click to copy donation XMR address)<br /><br />" +
				"Thank you!" +
				"</html>";
		return template.replace("{payPalDonationPage}",
				Utils.isDesktopActionSupported(Desktop.Action.BROWSE)
						? "click to navigate to PayPal donation page"
						: "click to copy PayPal donation page URL");
	}
}
