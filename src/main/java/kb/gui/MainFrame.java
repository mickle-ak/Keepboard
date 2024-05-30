package kb.gui;

import com.sun.jna.Platform;
import kb.ClipboardChangeListener;
import kb.ClipboardContent;
import kb.ClipboardItem;
import kb.ClipboardManager;
import kb.PreferencesPersistor;
import kb.RobotSupport;
import kb.SelectedClipboardContent;
import kb.clipboard.ClipboardAcessor;
import kb.gui.OkComponentPopupMenuHolder.PasteMode;
import kb.gui.prefs.MyPreferences;
import kb.gui.prefs.Size;
import kb.gui.search.SearchPanel;
import kb.gui.toolbar.ToolbarHandler;
import kb.gui.utils.MyTabComponent;
import kb.texts.Texts;
import kb.utils.Observable;
import kb.utils.Observer;
import kb.utils.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static MainFrame INSTANCE;
	
	private MainFrameMenuBarHolder menuBarHolder;
	private MainPanel mainPanel;
	private ItemGroupPanel itemGroupPanel;
	private SearchPanel searchPanel;
	private MyTabComponent searchTabComponent;
	private JTabbedPane tabbedPane;
	private ContentPreviewPanel contentPreviewPanel;
	private MainFrameButtonsPanel buttonsPanel;
	private OkComponentPopupMenuHolder okComponentPopupMenuHolder;
	private MainFramePanel beforeSearchSelectedPanel;
	private MainFramePanel selectedPanel;
	private JSplitPane splitPane;
	private DividerLocationCalculator dividerLocationCalculator;
	private boolean dividerVertical;
	private ChangeListener tabbedPaneChangeListener;
	private boolean tabsVisible;
	private Component toolbarPanel;
	
	private List<JDialog> dialogs = new ArrayList<JDialog>();
	private Map<Observer<?>, Observable<?>> observersMap = new HashMap<Observer<?>, Observable<?>>();
	
	private MainFrame() {
		// Empty constructor
	}
	
	private void init() {
		initFromPrefs();
		createComponents();
		addComponents();
		initSplitPaneDividerLocation();
		initWindow();
		addActions();
		initContentPreviewArea();
	}

	private void initSplitPaneDividerLocation() {
		dividerLocationCalculator.initDividerLocation();
	}

	private void initContentPreviewArea() {
		contentPreviewPanel.setContent(selectedPanel.getSelectedItem());
	}
	
	private void initFromPrefs() {
		tabsVisible = !MyPreferences.INSTANCE.areTabsHidden();
		dividerVertical = isDividerVertical(MyPreferences.INSTANCE.getPreviewAreaPosition());
	}

	private boolean isDividerVertical(PreviewAreaPosition previewAreaPosition) {
		return previewAreaPosition == PreviewAreaPosition.BOTTOM || previewAreaPosition == PreviewAreaPosition.TOP;
	}

	public static void reinitMainFrame() {
		INSTANCE.hideFrame();
		DialogClosingCommandsExecutor.INSTANCE.removeAllCommands();
		INSTANCE.searchPanel.stopSearch();
		INSTANCE.removeObservers();
		INSTANCE.disposeRegisteredDialogs();
		INSTANCE.dispose();
		initInstance();
		ShortcutKeyItem.reinitUIRelatedShortcuts();
		INSTANCE.showFrame();
	}

	@SuppressWarnings("unchecked")
	private void removeObservers() {
		for (@SuppressWarnings("rawtypes") Observer observer : observersMap.keySet()) {
			observersMap.get(observer).removeObserver(observer);
		}
	}

	private void disposeRegisteredDialogs() {
		for (JDialog dialog : dialogs) {
			dialog.dispose();
		}
	}

	private void addActions() {
		addApprovalObserver(mainPanel);
		addSelectionObserver(mainPanel);
		
		addApprovalObserver(itemGroupPanel);
		addSelectionObserver(itemGroupPanel);
		
		addApprovalObserver(searchPanel);
		addSelectionObserver(searchPanel);
		
		addButtonsActions();
		
		dividerLocationCalculator.addActions();
		
		addFrameClosingCommands();
	}

	private void addFrameClosingCommands() {
		DialogClosingCommandsExecutor.INSTANCE.addCommand(createFrameClosingCommand());
	}

	private DialogClosingCommand createFrameClosingCommand() {
		return new DialogClosingCommand() {
			@Override
			public void execute() {
				executeFrameClosingCommand();
			}
		};
	}

	private void executeFrameClosingCommand() {
		if (getExtendedState() == NORMAL) {
			executeFrameResizeCommand();
			executeDividerLocationChangeCommand();
		}
	}

	private void executeDividerLocationChangeCommand() {
		int oldDividerLocation = MyPreferences.INSTANCE.getDividerLocation();
		int newDividerLocation = splitPane.getDividerLocation();
		if (oldDividerLocation != newDividerLocation) {
			MyPreferences.INSTANCE.setDividerLocation(newDividerLocation);
			new PreferencesPersistor().storeToDisk();
		}
	}

	private void executeFrameResizeCommand() {
		Size oldMainFrameSize = MyPreferences.INSTANCE.getMainFrameSize();
		Size newMainFrameSize = new Size(getWidth(), getHeight());
		if (!oldMainFrameSize.equals(newMainFrameSize)) {
			MyPreferences.INSTANCE.setMainFrameSize(newMainFrameSize);
			new PreferencesPersistor().storeToDisk();
		}
	}
	
	private void addSelectionObserver(final MainFramePanel mainFramePanel) {
		mainFramePanel.getSelectionObservable().addObserver(new Observer<ClipboardItem>() {
			@Override
			public void update(ClipboardItem clipboardItem) {
				if (selectedPanel == mainFramePanel) {
					contentPreviewPanel.setContent(clipboardItem);
				}
			}
		});
	}

	private void addTabbedPaneChangeListener() {
		tabbedPane.addChangeListener(tabbedPaneChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				tabbedPaneChanged();
			}
		});
	}
	
	private void tabbedPaneChanged() {
		selectedPanel = (MainFramePanel) tabbedPane.getSelectedComponent();
		selectedPanelChanged();
	}

	private void addButtonsActions() {
		buttonsPanel.getBtnCancel().addActionListener(createButtonCancelActionListener());
		buttonsPanel.getBtnOk().addActionListener(createButtonOkActionListener());
		buttonsPanel.getOkComponent().getArrow().setMnemonic(KeyEvent.VK_D);
	}

	private ActionListener createButtonOkActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemChosen((e.getModifiers() & InputEvent.SHIFT_MASK) != 0);
			}
		};
	}

	private ActionListener createButtonCancelActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideFrame();
			}
		};
	}
	
	private void addApprovalObserver(MainFramePanel mainFramePanel) {
		mainFramePanel.getApprovalObservable().addObserver(new Observer<SelectedClipboardContent>() {
			@Override
			public void update(SelectedClipboardContent data) {
				itemChosen(data.isOnlyCopyToClipboard());
			}
		});
	}
	
	private void itemChosen(boolean onlyCopyToClipboard) {
		ClipboardContent selectedClipboardItemContent = selectedPanel.getSelectedClipboardItemContent();
		if (selectedClipboardItemContent != null) {
			hideFrame();
			executePasteMode(selectedClipboardItemContent, onlyCopyToClipboard);
			selectedPanel.moveSelectedToTop();
			if (selectedPanel != mainPanel) {
				ClipboardManager.getInstance().clipboardChanged(selectedClipboardItemContent);
			}
		}
	}

	private void executePasteMode(ClipboardContent selectedClipboardItemContent, boolean onlyCopyToClipboard) {
		if (onlyCopyToClipboard || okComponentPopupMenuHolder.getPasteMode() == PasteMode.PUT_IN_CLIPBOARD) {
			ClipboardAcessor.writeToClipboard(selectedClipboardItemContent);
		} else {
			ClipboardAcessor.writeToClipboardAndPaste(selectedClipboardItemContent);
		}
	}
	
	public void registerEscapeKey() {
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), "myEscape");
	}

	private void registerEscapeAction() {
		getRootPane().getActionMap().put("myEscape", createEscapeAction());
	}

	private AbstractAction createEscapeAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				hideFrame();
				if (Platform.isMac() && okComponentPopupMenuHolder.getPasteMode() == PasteMode.PASTE) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Utils.sleepIgnoreInterrupt(50);
							RobotSupport.submitTypeKey(MyPreferences.INSTANCE.getFocusActiveWindowKey());
						}
					});
				}
			}
		};
	}
	
	public void unregisterEscapeKey() {
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), null);
	}

	private void initWindow() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle(Texts.PROGRAM_NAME);
		this.setIconImage(Utils.getImage("myIcon.png"));
		this.addWindowListener(getWindowClosingListener());
		setSize();
		setLocation();
		registerEscapeAction();
		registerEscapeKey();
	}

	private void setSize() {
		Size size = MyPreferences.INSTANCE.getMainFrameSize();
		this.setSize(size.getWidth(), size.getHeight());
	}

	private void setLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
	}

	private WindowListener getWindowClosingListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				hideFrame();
			}
		};
	}

	private void createComponents() {
		mainPanel = new MainPanel(this);
		itemGroupPanel = new ItemGroupPanel();
		searchPanel = new SearchPanel();
		searchTabComponent = new MyTabComponent("Search", KeyEvent.VK_R, createSearchPanelCloseAction());
		menuBarHolder = new MainFrameMenuBarHolder();
		tabbedPane = new JTabbedPane();
		contentPreviewPanel = new ContentPreviewPanel();
		buttonsPanel = new MainFrameButtonsPanel(createOkComponentPopupMenu());
		selectedPanel = mainPanel;
		createSplitPane();
		dividerLocationCalculator = new DividerLocationCalculator(splitPane);
		toolbarPanel = ToolbarHandler.getInstance().getToolbarPanel();
		
		if (tabsVisible) {
			addTabbedPaneComponents();
		}
	}
	
	private void addTabbedPaneComponents() {
		tabbedPane.addTab("Clipboard history", mainPanel);
		tabbedPane.addTab("Saved items", itemGroupPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_C);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_V);
		addTabbedPaneChangeListener();
	}

	private Action createSearchPanelCloseAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchPanel.stopSearch();
				
				boolean wasSearchPanelSelected = tabbedPane.getSelectedComponent() == searchPanel;
				
				tabbedPane.removeTabAt(2);
				
				if (wasSearchPanelSelected) {
					setSelectedPanel(beforeSearchSelectedPanel);
				}
			}
		};
	}

	private JPopupMenu createOkComponentPopupMenu() {
		okComponentPopupMenuHolder = new OkComponentPopupMenuHolder();
		return okComponentPopupMenuHolder.getPopupMenu();
	}

	public void hideFrame() {
		setExtendedState(NORMAL);
		DialogClosingCommandsExecutor.INSTANCE.executeCommands();
		setVisible(false);
	}

	private void addComponents() {
		getContentPane().setLayout(new GridBagLayout());
		
		if (!MyPreferences.INSTANCE.isToolbarHidden()) {
			addToolbar();
		}
		
		addSplitPane();
		addMenu();
		
		if (!MyPreferences.INSTANCE.areButtonsHidden()) {
			addButtonsPanel();
		}
	}

	private void addToolbar() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		this.add(toolbarPanel, c);
	}

	private void addButtonsPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		this.add(buttonsPanel, c);
	}

	private void addSplitPane() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(splitPane, c);
	}

	private void createSplitPane() {
		splitPane = new JSplitPane();
		
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		
		initSplitPane(MyPreferences.INSTANCE.getPreviewAreaPosition());
	}

	private void initSplitPane(PreviewAreaPosition previewAreaPosition) {
		int orientation = (previewAreaPosition == PreviewAreaPosition.TOP || previewAreaPosition == PreviewAreaPosition.BOTTOM)
				? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT;
		
		Component firstComponent;
		Component secondComponent;
		Component itemsPanel = tabsVisible ? tabbedPane : mainPanel;
		
		if (previewAreaPosition == PreviewAreaPosition.TOP || previewAreaPosition == PreviewAreaPosition.LEFT) {
			firstComponent = contentPreviewPanel;
			secondComponent = itemsPanel;
		} else {
			firstComponent = itemsPanel;
			secondComponent = contentPreviewPanel;
		}
		
		splitPane.setLeftComponent(null);
		splitPane.setRightComponent(null);
		
		splitPane.setOrientation(orientation);
		splitPane.setLeftComponent(firstComponent);
		splitPane.setRightComponent(secondComponent);
	}

	private void addMenu() {
		this.setJMenuBar(menuBarHolder.createAndInitMenuBar());
	}

	public void showFrame() {
		setVisible(false);
		clearFilters();
		setVisible(true);
		if (Platform.isMac()) {
//		Desktop.getDesktop().requestForeground(false);
			try {
				Class<?> c = Class.forName("sun.lwawt.macosx.LWCToolkit");
				Method method = c.getDeclaredMethod("activateApplicationIgnoringOtherApps");
				method.invoke(Toolkit.getDefaultToolkit());
			} catch (Throwable t) {
				// Ignored
			}
		}

		selectedPanel.panelDisplayed();
	}

	private void clearFilters() {
		mainPanel.clearFilter();
		itemGroupPanel.clearFilter();
	}
	
	public void displayErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public boolean displayConfirmationMessage(String message) {
		return JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	public static void initInstance() {
		INSTANCE = new MainFrame();
		INSTANCE.init();
	}
	
	public static MainFrame getInstance() {
		return INSTANCE;
	}

	public void continueClipboardChangeTracking() {
		ClipboardChangeListener.getInstance().continueListening();
		menuBarHolder.clipboardChangeTrackingContinued();
	}

	public void pauseClipboardChangeTracking() {
		ClipboardChangeListener.getInstance().pauseListening();
		menuBarHolder.clipboardChangeTrackingPaused();
	}
	
	public void goToItemGroup(String name) {
		setSelectedPanel(itemGroupPanel);
		itemGroupPanel.selectGroup(name);
	}
	
	private void setSelectedPanel(MainFramePanel mainFramePanel) {
		boolean wasPanelSelected = selectedPanel == mainFramePanel;
		
		selectedPanel = mainFramePanel;
		
		if (tabsVisible) {
			tabbedPane.setSelectedComponent(selectedPanel);
			if (wasPanelSelected) {
				selectedPanel.panelDisplayed();
			}
		} else {
			setItemsComponentInSplitPane(selectedPanel);
			validate();
			selectedPanelChanged();
			dividerLocationCalculator.setDividerLocation();
		}
	}

	private void selectedPanelChanged() {
		selectedPanel.panelDisplayed();
		contentPreviewPanel.setContent(selectedPanel.getSelectedItem());
	}

	public void goToClipboardHistory() {
		setSelectedPanel(mainPanel);
	}
	
	public MainFrameMenuBarHolder getMenuBarHolder() {
		return menuBarHolder;
	}

	public MyTabComponent getSearchTabComponent() {
		return searchTabComponent;
	}

	public void registerDialog(JDialog dialog) {
		dialogs.add(dialog);
	}
	
	public void registerObserver(Observer<?> observer, Observable<?> observable) {
		observersMap.put(observer, observable);
	}
	
	public void showSearchPane() {
		if (tabsVisible && tabbedPane.getTabCount() < 3) {
			addSearchTab();
		}
		
		if (selectedPanel != searchPanel) {
			beforeSearchSelectedPanel = selectedPanel;
		}
		
		setSelectedPanel(searchPanel);
	}

	private void addSearchTab() {
		tabbedPane.addTab("", searchPanel);
		tabbedPane.setTabComponentAt(2, searchTabComponent);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_R);
	}
	
	public void setTabsVisible(boolean tabsVisible) {
		if (this.tabsVisible == tabsVisible) {
			return;
		}
		
		DialogClosingCommandsExecutor.INSTANCE.executeCommands();
		
		this.tabsVisible = tabsVisible;
		
		if (tabsVisible) {
			setItemsComponentInSplitPane(tabbedPane);
			addTabbedPaneComponents();
			if (selectedPanel == searchPanel) {
				addSearchTab();
			}
			validate();
			dividerLocationCalculator.setDividerLocation();
		} else {
			tabbedPane.removeChangeListener(tabbedPaneChangeListener);
			tabbedPane.removeAll();
		}
		
		setSelectedPanel(selectedPanel);
	}
	
	private void setItemsComponentInSplitPane(Component itemsComponent) {
		PreviewAreaPosition previewAreaPosition = MyPreferences.INSTANCE.getPreviewAreaPosition();
		if (previewAreaPosition == PreviewAreaPosition.RIGHT || previewAreaPosition == PreviewAreaPosition.BOTTOM) {
			splitPane.setLeftComponent(itemsComponent);
		} else {
			splitPane.setRightComponent(itemsComponent);
		}
	}
	
	public void setButtonsVisible(boolean buttonsVisible) {
		DialogClosingCommandsExecutor.INSTANCE.executeCommands();
		
		if (buttonsVisible) {
			addButtonsPanel();
		} else {
			this.remove(buttonsPanel);
		}
		
		validate();
		dividerLocationCalculator.setDividerLocationAfterButtonsVisibilityChange(buttonsVisible);
	}
	
	public void setGroupComboBoxVisible(boolean visible) {
		itemGroupPanel.setGroupComboBoxVisible(visible);
	}

	public void showItemsGroupPanel() {
		setSelectedPanel(itemGroupPanel);
	}
	
	public void moveSelectedItemUp() {
		selectedPanel.moveItemUp();
	}
	
	public void moveSelectedItemDown() {
		selectedPanel.moveItemDown();
	}
	
	public void copySelectedItemsToGroup() {
		selectedPanel.copySelectedItemsToGroup();
	}
	
	public void deleteSelectedItems() {
		selectedPanel.deleteSelectedItems();
	}
	
	public void openSelectedItem() {
		selectedPanel.openSelectedItem();
	}
	
	public void createNewItem() {
		selectedPanel.createNewItem();
	}

	public void setToolbarVisible(boolean visible) {
		DialogClosingCommandsExecutor.INSTANCE.executeCommands();
		
		if (visible) {
			addToolbar();
		} else {
			this.remove(toolbarPanel);
		}
		
		validate();
		dividerLocationCalculator.setDividerLocationAfterToolbarVisibilityChange(visible);
	}
	
	public void setPreviewAreaPosition(PreviewAreaPosition previewAreaPosition) {
		int contentAreaWidth = contentPreviewPanel.getWidth();
		
		initSplitPane(previewAreaPosition);
		
		if (dividerVertical && !isDividerVertical(previewAreaPosition)) {
			setSize(getWidth() + 400, getHeight());
		} else if (!dividerVertical && isDividerVertical(previewAreaPosition)) {
			setSize(getWidth() - contentAreaWidth, getHeight());
		}
		
		dividerVertical = isDividerVertical(previewAreaPosition);
		
		contentPreviewPanel.adjustLineWrapCheckBoxVisibility();
		
		validate();
		
		dividerLocationCalculator.setDividerLocationAfterPreviewAreaPositionChange();
	}
}
