package kb.gui.prefs;

import kb.LookAndFeelHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class LookAndFeelPreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	private JRadioButton rbNimbus;
	private JRadioButton rbSystem;
	private JRadioButton rbMetal;
	
	private LookAndFeelPref oldValue;
	
	private Map<JRadioButton, LookAndFeelPref> map = new HashMap<JRadioButton, LookAndFeelPref>();
	
	public LookAndFeelPreferencePanel() {
		addComponents();
		initComponents();
		addActions();
		setToolTips();
		disableNimbusIfNotSupported();
	}

	private void setToolTips() {
		rbNimbus.setToolTipText("Nimbus look and feel.");
		rbSystem.setToolTipText("Native system look and feel.");
		rbMetal.setToolTipText("Default Java cross platform look and feel.");
	}

	private void disableNimbusIfNotSupported() {
		if (!LookAndFeelHandler.INSTANCE.isNimbusSupported()) {
			rbNimbus.setEnabled(false);
			rbNimbus.setToolTipText("<html>Nimbus look and feel is not supported.<br />Please, check whether you have Java 6 Update 10 or newer version of Java.</html>");
		}
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		int row = 0;
		add(rbNimbus = new JRadioButton("Nimbus"), createGridBagConstraints(row++));
		add(rbSystem = new JRadioButton("System"), createGridBagConstraints(row++));
		add(rbMetal = new JRadioButton("Metal"), createGridBagConstraints(row++));
		
		addRadioButtonsToGroup();
	}
	
	private GridBagConstraints createGridBagConstraints(int gridy) {
		GridBagConstraints result = new GridBagConstraints();
		result.gridx = 0; result.gridy = gridy;
		result.weightx = 1;
		result.fill = GridBagConstraints.HORIZONTAL;
		return result;
	}
	
	private void addRadioButtonsToGroup() {
		addButtonToGroup(rbNimbus, LookAndFeelPref.NIMBUS);
		addButtonToGroup(rbSystem, LookAndFeelPref.SYSTEM);
		addButtonToGroup(rbMetal, LookAndFeelPref.METAL);
	}
	
	private void addButtonToGroup(JRadioButton button, LookAndFeelPref lookAndFeelPref) {
		buttonGroup.add(button);
		map.put(button, lookAndFeelPref);
	}

	private void initComponents() {
		LookAndFeelPref currentLookAndFeel = MyPreferences.INSTANCE.getLookAndFeelPref(); 
		getButton(currentLookAndFeel).setSelected(true);
		oldValue = currentLookAndFeel;
	}

	private JRadioButton getButton(LookAndFeelPref lookAndFeelPref) {
		for (JRadioButton button : map.keySet()) {
			if (map.get(button) == lookAndFeelPref) {
				return button;
			}
		}
		throw new NoSuchElementException();
	}

	private void addActions() {
		ActionListener selectionListener = createSelectionListener();
		for (JRadioButton button : map.keySet()) {
			button.addActionListener(selectionListener);
		}
	}

	private ActionListener createSelectionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateChangeObservable();
			}
		};
	}
	
	private void updateChangeObservable() {
		changeObservable.updateObservers(isValueChanged() ? PreferenceChangeState.CHANGED : PreferenceChangeState.OLD_VALUE);
	}

	private boolean isValueChanged() {
		return getCurrentValue() != oldValue;
	}

	private LookAndFeelPref getCurrentValue() {
		return map.get(getSelectedButton());
	}

	private JRadioButton getSelectedButton() {
		for (JRadioButton button : map.keySet()) {
			if (button.isSelected()) {
				return button;
			}
 		}
		throw new NoSuchElementException();
	}

	@Override
	public void applyChanges() {
		LookAndFeelPref currentValue = getCurrentValue();
		MyPreferences.INSTANCE.setLookAndFeelPref(currentValue);
		LookAndFeelHandler.INSTANCE.updateLookAndFeel(currentValue);
		oldValue = currentValue;
	}

	@Override
	public void clearChanges() {
		getButton(oldValue).setSelected(true);
		updateChangeObservable();
	}
}
