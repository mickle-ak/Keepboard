package kb.gui.prefs;

import com.sun.jna.Platform;
import kb.RobotSupport;
import kb.gui.GuiUtils;
import kb.gui.Key;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class AutopastePreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;

	private JLabel lblAutopasteKey;
	private JTextField txtAutopasteKey;
	
	private Key oldValue;
	private Key currentKey;
	
	public AutopastePreferencePanel() {
		addComponents();
		initComponents();
		addActions();
	}
	
	private void addActions() {
		addTxtAutopasteKeyListener();
	}

	private void updateChangeObervable() {
		changeObservable.updateObservers(isValueChanged() ? PreferenceChangeState.CHANGED : PreferenceChangeState.OLD_VALUE);
	}

	private boolean isValueChanged() {
		return !oldValue.equals(currentKey);
	}

	private void addTxtAutopasteKeyListener() {
		txtAutopasteKey.addKeyListener(new KeyAdapter() {
			private Key lastPressedKey;
			
			@Override
			public void keyPressed(KeyEvent e) {
				lastPressedKey = new Key(e.getModifiers(), e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				tableKeyTyped(lastPressedKey);
			}
		});
	}

	private void tableKeyTyped(Key key) {
		currentKey = key;
		txtAutopasteKey.setText(key.toText());
		updateChangeObervable();
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addLblAutopasteKey();
		addTxtAutopasteKey();
	}

	private void addLblAutopasteKey() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(lblAutopasteKey = new JLabel("Paste key: "), c);
	}

	private void addTxtAutopasteKey() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		add(txtAutopasteKey = createTxtAutopasteKey(), c);
	}

	private JTextField createTxtAutopasteKey() {
		 JTextField result = new JTextField();
		 result.setEditable(false);
		 GuiUtils.setSize(result, new Dimension(150, 25));
		 result.setHorizontalAlignment(SwingConstants.CENTER);
		 return result;
	}

	private void initComponents() {
		oldValue = MyPreferences.INSTANCE.getAutopasteKey();
		initComponentsState();
		initToolTips();
		checkIsAutopasteSupported();
	}

	private void initToolTips() {
		setAutopasteKeyToolTip(
				"<html>This will be the key combination that Keepboard will send to the currently<br />" +
				"focused application after an item is selected to be pasted automatically<br />" +
				"(<i>OK</i> button configuration on the main frame).</html>");
	}

	private void initComponentsState() {
		txtAutopasteKey.setText(oldValue.toText());
		currentKey = oldValue;
	}
	
	private void checkIsAutopasteSupported() {
		if (!RobotSupport.isRobotSupported()) {
			setAutopasteKeyComponentsEnabled(false);
			setAutopasteKeyToolTip(getRobotNotSupportedErrorMessage());
		}
	}

	private void setAutopasteKeyToolTip(String toolTip) {
		lblAutopasteKey.setToolTipText(toolTip);
		txtAutopasteKey.setToolTipText(toolTip);
	}

	private String getRobotNotSupportedErrorMessage() {
		String result = "<html>Program cannot send paste events to other applications on this platform.";
		if (Platform.isX11()) {
			result += "<br />Check whether XTEST 2.2 standard extension is supported (or enabled).";
		}
		return result + "</html>";
	}
	
	private void setAutopasteKeyComponentsEnabled(boolean enabled) {
		lblAutopasteKey.setEnabled(enabled);
		txtAutopasteKey.setEnabled(enabled);
	}

	@Override
	public void applyChanges() {
		oldValue = currentKey;
		MyPreferences.INSTANCE.setAutopasteKey(currentKey);
	}

	@Override
	public void clearChanges() {
		txtAutopasteKey.setText(oldValue.toText());
		currentKey = oldValue;
		updateChangeObervable();
	}
}
