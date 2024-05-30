package kb.gui;

import kb.ClipboardManager;
import kb.gui.prefs.MyPreferences;
import kb.texts.Texts;
import kb.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SystemTrayHandler {
	private static final SystemTrayHandler INSTANCE = new SystemTrayHandler();
	
	private TrayIcon trayIcon;
	private TrayIcon pausedTrayIcon;
	private TrayIcon selectedIcon;
	
	private SystemTrayHandler() {
		// private constructor
	}
	
	public static SystemTrayHandler getInstance() {
		return INSTANCE;
	}
	
	public void showInSystemTray() {
		if (!isSystemTraySupported()) {
			return;
		}
		
		createAndInitTrayIcons();
		selectedIcon = MyPreferences.INSTANCE.isPaused() ? pausedTrayIcon : trayIcon;
		addSelectedIconIfVisible();
	}
	
	public boolean isSystemTraySupported() {
		return SystemTray.isSupported();
	}

	private void addSelectedIconIfVisible() {
		if (!MyPreferences.INSTANCE.isTrayIconHidden()) {
			addIconToSystemTray(selectedIcon);
		}
	}

	private void createAndInitTrayIcons() {
		trayIcon = createTrayIcon("myIcon.JPG", Texts.PROGRAM_NAME);
		pausedTrayIcon = createTrayIcon("myIconPaused.JPG", Texts.PAUSED_STATE_TRAY_TOOLTIP);
	}
	
	private TrayIcon createTrayIcon(String imageName, String tooltip) {
		Image image = Utils.getImage(imageName);
		if (isSystemTraySupported()) {
			Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
			image = new ImageIcon(image).getImage().getScaledInstance(trayIconSize.width, trayIconSize.height, Image.SCALE_SMOOTH);
//			image = Utils.scale(new ImageIcon(image).getImage(), trayIconSize.width - 2, trayIconSize.height - 2);
		}
		TrayIcon result = new TrayIcon(image, tooltip);
//		result.setImageAutoSize(true);
		result.setPopupMenu(createTrayPopupMenu());
		result.addActionListener(new ShowMainFrameActionListener());
		return result;
	}

	private void addIconToSystemTray(TrayIcon trayIcon) {
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException ignored) {
			// Ignored
		}
	}
	
	private PopupMenu createTrayPopupMenu() {
		PopupMenu result = new PopupMenu();
		result.add(createShowMainFrameMenuItem());
		result.addSeparator();
		result.add(createExitMenuItem());
		return result;
	}

	private MenuItem createShowMainFrameMenuItem() {
		MenuItem result = new MenuItem("Show Keepboard");
		result.addActionListener(new ShowMainFrameActionListener());
		return result;
	}

	private MenuItem createExitMenuItem() {
		MenuItem result = new MenuItem("Exit");
		result.addActionListener(new ExitActionListener());
		return result;
	}
	
	private static class ShowMainFrameActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ClipboardManager.getInstance().showMainFrame();
		}
	}
	
	public void setTrayIcon(boolean paused) {
		if (!isSystemTraySupported()) {
			return;
		}
		
		selectedIcon = paused ? pausedTrayIcon : trayIcon;
		if (!MyPreferences.INSTANCE.isTrayIconHidden()) {
			changeTrayIcon(paused);
		}
	}

	private void changeTrayIcon(boolean paused) {
		if (paused) {
			changeTrayIcon(trayIcon, pausedTrayIcon);
		} else {
			changeTrayIcon(pausedTrayIcon, trayIcon);
		}
	}

	private void changeTrayIcon(TrayIcon oldIcon, TrayIcon newIcon) {
		SystemTray.getSystemTray().remove(oldIcon);
		addIconToSystemTray(newIcon);
	}
	
	public void setTrayIconVisible(boolean visible) {
		if (!isSystemTraySupported()) {
			return;
		}
		
		if (visible) {
			addIconToSystemTray(selectedIcon);
		} else {
			SystemTray.getSystemTray().remove(selectedIcon);
		}
	}
}
