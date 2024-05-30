package kb.gui.prefs;

import kb.ItemGroupHolder;
import kb.gui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MaxHistorySizePreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;

	private JLabel lblMaxHistorySize;
	private JComboBox cmbMaxHistorySize;
	
	private int oldValue;
	
	public MaxHistorySizePreferencePanel() {
		addComponents();
		initComponents();
		addActions();
	}
	
	private void addActions() {
		addCmbMaxHistorySizeListener();
	}

	private void updateChangeObervable() {
		changeObservable.updateObservers(isValueChanged() ? PreferenceChangeState.CHANGED : PreferenceChangeState.OLD_VALUE);
	}

	private boolean isValueChanged() {
		return oldValue != getSelectedValue();
	}
	
	private int getSelectedValue() {
		return (Integer) cmbMaxHistorySize.getSelectedItem();
	}

	private void addCmbMaxHistorySizeListener() {
		cmbMaxHistorySize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateChangeObervable();
			}
		});
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addLblMaxHistorySize();
		addCmbMaxHistorySize();
	}

	private void addLblMaxHistorySize() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(lblMaxHistorySize = new JLabel("Max history size: "), c);
	}

	private void addCmbMaxHistorySize() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		add(cmbMaxHistorySize = createCmbMaxHistorySize(), c);
	}

	private JComboBox createCmbMaxHistorySize() {
		JComboBox result = new JComboBox();
		result.setRenderer(createCmbMaxHistorySizeCellRenderer());
		GuiUtils.setSize(result, new Dimension(80, 25));
		return result;
	}

	private ListCellRenderer createCmbMaxHistorySizeCellRenderer() {
		DefaultListCellRenderer result = new DefaultListCellRenderer();
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		return result;
	}

	private void initComponents() {
		oldValue = MyPreferences.INSTANCE.getMaxHistorySize();
		initComponentsState();
		initToolTips();
	}

	private void initToolTips() {
		String tooltip = "Maximum items count kept in clipboard history.";
		cmbMaxHistorySize.setToolTipText(tooltip);
		lblMaxHistorySize.setToolTipText(tooltip);
	}

	private void initComponentsState() {
		addItemsToCmbMaxHistorySize();
		cmbMaxHistorySize.setSelectedItem(Integer.valueOf(oldValue));
	}

	private void addItemsToCmbMaxHistorySize() {
		cmbMaxHistorySize.addItem(Integer.valueOf(1));
		cmbMaxHistorySize.addItem(Integer.valueOf(2));
		cmbMaxHistorySize.addItem(Integer.valueOf(3));
		cmbMaxHistorySize.addItem(Integer.valueOf(5));
		cmbMaxHistorySize.addItem(Integer.valueOf(10));
		cmbMaxHistorySize.addItem(Integer.valueOf(20));
		cmbMaxHistorySize.addItem(Integer.valueOf(30));
		cmbMaxHistorySize.addItem(Integer.valueOf(50));
		cmbMaxHistorySize.addItem(Integer.valueOf(100));
		cmbMaxHistorySize.addItem(Integer.valueOf(200));
		cmbMaxHistorySize.addItem(Integer.valueOf(300));
		cmbMaxHistorySize.addItem(Integer.valueOf(500));
		cmbMaxHistorySize.addItem(Integer.valueOf(1000));
		cmbMaxHistorySize.addItem(Integer.valueOf(2000));
		cmbMaxHistorySize.addItem(Integer.valueOf(3000));
		cmbMaxHistorySize.addItem(Integer.valueOf(5000));
		cmbMaxHistorySize.addItem(Integer.valueOf(10000));
	}
	
	@Override
	public void applyChanges() {
		oldValue = getSelectedValue();
		MyPreferences.INSTANCE.setMaxHistorySize(oldValue);
		ItemGroupHolder.getInstance().getClipboardHistory().setMaxGroupSize(oldValue);
	}

	@Override
	public void clearChanges() {
		cmbMaxHistorySize.setSelectedItem(Integer.valueOf(oldValue));
		updateChangeObervable();
	}
}
