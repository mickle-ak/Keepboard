package kb.gui;

import kb.ClipboardManager;
import kb.gui.prefs.platf.Snap;
import kb.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JButton btnAppHomeFolder;
	private JButton btnClose;
	
	public AboutDialog(AboutPanel.FocusedTab focusedTab) {
		super(MainFrame.getInstance(), "About Keepboard", true);
		addComponents(focusedTab);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addActions();
		setSize(400, 420);
		setLocationRelativeTo(MainFrame.getInstance());
		setVisible(true);
	}

	private void addActions() {
		registerEscapeKey();
		addComponentListener();
	}
	
	private void addComponentListener() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				btnClose.requestFocusInWindow();
			}
		});
	}
	
	private void registerEscapeKey() {
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), "myEscape");
		
		getRootPane().getActionMap().put("myEscape", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				dispose();
			}
		});
	}
	
	private void addComponents(AboutPanel.FocusedTab focusedTab) {
		setLayout(new GridBagLayout());
		addAboutPanel(focusedTab);
		addButtonsPanel();
	}

	private void addAboutPanel(AboutPanel.FocusedTab focusedTab) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		add(new AboutPanel(focusedTab), c);
	}

	private void addButtonsPanel() {
		addAppHomeFolderButton();
		addCloseButton();
	}

	private void addAppHomeFolderButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.insets = new Insets(0, 5, 5, 0);
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		add(createButtonAppHomeFolder(), c);
	}

	private Component createButtonAppHomeFolder() {
		btnAppHomeFolder = new JButton("Data folder");
		btnAppHomeFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayAppHomeFolder();
			}
		});
		GuiUtils.setSize(btnAppHomeFolder, new Dimension(150, 30));
		return btnAppHomeFolder;
	}
	
	private void displayAppHomeFolder() {
		if (Utils.isDesktopActionSupported(Desktop.Action.OPEN)) {
			try {
				Desktop.getDesktop().open(new File(getDataFolderPath()));
			} catch (Exception e) {
				displayAndCopyDataFolderPath();
			}
		} else {
			displayAndCopyDataFolderPath();
		}
	}

	private String getDataFolderPath() {
		return Snap.isAppRunningInSnapEnvironment()
				? Snap.getSnapUserCommonDirectory()
				: System.getProperty("user.dir");
	}

	private void displayAndCopyDataFolderPath() {
		String dataFolderPath = getDataFolderPath();
		ClipboardManager.getInstance().copyToClipboard(dataFolderPath);
		JOptionPane.showMessageDialog(this, "<html>Keepboard data folder is: <b>" + dataFolderPath + "</b>.<br />This path has been copied to clipboard.</html>",
				"Data folder", JOptionPane.INFORMATION_MESSAGE);
	}

	private void addCloseButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 1;
		c.insets = new Insets(0, 0, 5, 5);
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		add(createButtonClose(), c);
	}

	private JButton createButtonClose() {
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		GuiUtils.setSize(btnClose, new Dimension(100, 30));
		return btnClose;
	}
}
