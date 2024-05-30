package kb.gui.prefs;

import kb.gui.prefs.platf.AutostartHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AutostartPreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;
	
	private JCheckBox chkAutostart;
	private boolean oldValue;
	
	public AutostartPreferencePanel() {
		addComponents();
		initComponents();
		addActions();
		setTooltip();
		checkAutostartSupported();
	}

	private void checkAutostartSupported() {
		if (!AutostartHandler.getInstance().isAutostartSupported()) {
			chkAutostart.setEnabled(false);
			chkAutostart.setToolTipText("<html>Unfortunately, Keepboard has been unable to determine how to set this option on your system.<br />"
					+ "If you would like Keepboard to start automatically, please add it manually to your startup programs list.</html>");
		}
	}

	private void setTooltip() {
		chkAutostart.setToolTipText("Start Keepboard automatically after system startup.");
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addChkAutostart();
	}

	private void addChkAutostart() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(chkAutostart = new JCheckBox("Start Keepboard automatically"), c);
	}

	private void initComponents() {
		oldValue = MyPreferences.INSTANCE.isAutostart();
		chkAutostart.setSelected(oldValue);
	}

	private void addActions() {
		chkAutostart.addActionListener(new ActionListener() {
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
		return chkAutostart.isSelected() != oldValue;
	}

	@Override
	public void applyChanges() {
		oldValue = chkAutostart.isSelected();
		MyPreferences.INSTANCE.setAutostart(oldValue);
		AutostartHandler.getInstance().setAutostart(oldValue);
	}

	@Override
	public void clearChanges() {
		chkAutostart.setSelected(oldValue);
		updateChangeObservable();
	}
}
