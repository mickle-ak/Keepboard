package kb.gui.prefs;

import com.sun.jna.Platform;
import kb.PreferencesPersistor;
import kb.gui.MyInputDialog;
import kb.utils.Observer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PreferencesDialog extends MyInputDialog<Void> {
	private static final long serialVersionUID = 1L;
	
	private static PreferencesDialog INSTANCE;
	
	private List<PreferencePanel> panels = new ArrayList<PreferencePanel>();
	
	private Set<PreferencePanel> changedPanels = new HashSet<PreferencePanel>();
	private Set<PreferencePanel> errorPanels = new HashSet<PreferencePanel>();
	
	private PreferencesDialog() {
		setTitle("Preferences");
		addDialogComponents();
		setSize(400, 450);
		addActions();
	}
	
	private void addActions() {
		for (PreferencePanel panel : panels) {
			panel.getChangeObservable().addObserver(createChangeObservable(panel));
		}
	}

	private Observer<PreferenceChangeState> createChangeObservable(final PreferencePanel panel) {
		return new Observer<PreferenceChangeState>() {
			@Override
			public void update(PreferenceChangeState data) {
				preferenceChanged(panel, data);
			}
		};
	}
	
	private void preferenceChanged(PreferencePanel panel, PreferenceChangeState preferenceChangeState) {
		switch(preferenceChangeState) {
		case CHANGED:
			changedPanels.add(panel);
			errorPanels.remove(panel);
			break;
		case OLD_VALUE:
			changedPanels.remove(panel);
			errorPanels.remove(panel);
			break;
		case ERROR:
			errorPanels.add(panel);
			changedPanels.remove(panel);
		}
		
		getOkButton().setEnabled(errorPanels.isEmpty() && !changedPanels.isEmpty());
	}

	public static void showDialog() {
		createInstanceIfNotCreated();
		INSTANCE.init();
		INSTANCE.setVisible(true);
	}

	private void init() {
		clearChanges();
	}
	
	private void clearChanges() {
		for (PreferencePanel panel : panels) {
			panel.clearChanges();
		}
	}

	@Override
	protected void inputConfirmed() {
		applyChanges();
		persistChanges();
		super.inputConfirmed();
	}

	private void persistChanges() {
		new PreferencesPersistor().storeToDisk();
	}

	private void applyChanges() {
		for (PreferencePanel panel : changedPanels) {
			panel.applyChanges();
		}
	}

	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new PreferencesDialog();
		}
	}

	@Override
	protected Void getValue() {
		return null;
	}

	@Override
	protected JPanel getValuePanel() {
		JPanel result = new JPanel(new GridBagLayout());
		
		int row = 0;
		if (!Platform.isMac()) {
			addPreferencePanel(new AutostartPreferencePanel(), result, "Auto-start", row++);
		}
		addPreferencePanel(new AutosaveImagesPreferencePanel(), result, "Auto-save images", row++);
		if (Platform.isMac()) {
			addPreferencePanel(new FocusActiveWindowPreferencePanel(), result, "Focus active window on close", row++);
		}
		addPreferencePanel(new AutopastePreferencePanel(), result, "Auto-paste", row++);
		addPreferencePanel(new MaxHistorySizePreferencePanel(), result, "History size", row++);
		addLastPreferencePanel(new LookAndFeelPreferencePanel(), result, "Look & Feel", row++);
		
		return result;
	}

	private void addPreferencePanel(PreferencePanel preferencePanel, JPanel toPanel, String name, int row) {
		panels.add(preferencePanel);
		toPanel.add(preferencePanel, createGridBagConstraints(row));
		preferencePanel.setBorder(BorderFactory.createTitledBorder(name));
	}

	private GridBagConstraints createGridBagConstraints(int gridy) {
		GridBagConstraints result = new GridBagConstraints();
		result.gridx = 0; result.gridy = gridy;
		result.weightx = 1;
		result.fill = GridBagConstraints.HORIZONTAL;
		return result;
	}
	
	private void addLastPreferencePanel(PreferencePanel preferencePanel, JPanel toPanel, String name, int row) {
		panels.add(preferencePanel);
		GridBagConstraints c =  createGridBagConstraints(row);
		c.weighty = 1; c.anchor = GridBagConstraints.NORTH;
		toPanel.add(preferencePanel, c);
		preferencePanel.setBorder(BorderFactory.createTitledBorder(name));
	}
}
