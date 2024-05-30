package kb.gui.prefs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AutosaveImagesPreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;
	
	private JCheckBox chkAutosave;
	private boolean oldValue;
	
	public AutosaveImagesPreferencePanel() {
		addComponents();
		initComponents();
		addActions();
		setTooltip();
	}

	private void setTooltip() {
		chkAutosave.setToolTipText("If selected, Keepboard will save images automatically after they appear in clipboard.");
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addChkAutosave();
	}

	private void addChkAutosave() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(chkAutosave = new JCheckBox("Save images automatically"), c);
	}

	private void initComponents() {
		oldValue = MyPreferences.INSTANCE.isAutosaveImages();
		chkAutosave.setSelected(oldValue);
	}

	private void addActions() {
		chkAutosave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				updateChangeObservable();
			}
		});
	}
	
	private void updateChangeObservable() {
		changeObservable.updateObservers(isValueChanged() ? PreferenceChangeState.CHANGED : PreferenceChangeState.OLD_VALUE);
	}

	private boolean isValueChanged() {
		return chkAutosave.isSelected() != oldValue;
	}

	@Override
	public void applyChanges() {
		oldValue = chkAutosave.isSelected();
		MyPreferences.INSTANCE.setAutosaveImages(chkAutosave.isSelected());
	}

	@Override
	public void clearChanges() {
		chkAutosave.setSelected(oldValue);
		updateChangeObservable();
	}
}
