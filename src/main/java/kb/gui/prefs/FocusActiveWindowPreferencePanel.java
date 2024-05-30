package kb.gui.prefs;

import com.sun.jna.Platform;
import kb.RobotSupport;
import kb.gui.GuiUtils;
import kb.gui.Key;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class FocusActiveWindowPreferencePanel extends PreferencePanel {
	private static final long serialVersionUID = 1L;

	private JLabel lblFocusActiveWindowKey;
	private JTextField txtFocusActiveWindowKey;

	private Key oldValue;
	private Key currentKey;

	public FocusActiveWindowPreferencePanel() {
		addComponents();
		initComponents();
		addActions();
	}
	
	private void addActions() {
		addTxtFocusActiveWindowKeyListener();
	}

	private void updateChangeObervable() {
		changeObservable.updateObservers(isValueChanged() ? PreferenceChangeState.CHANGED : PreferenceChangeState.OLD_VALUE);
	}

	private boolean isValueChanged() {
		return !oldValue.equals(currentKey);
	}

	private void addTxtFocusActiveWindowKeyListener() {
		txtFocusActiveWindowKey.addKeyListener(new KeyAdapter() {
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
		txtFocusActiveWindowKey.setText(key.toText());
		updateChangeObervable();
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addLblFocusActiveWindowKey();
		addTxtFocusActiveWindowKey();
	}

	private void addLblFocusActiveWindowKey() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		add(lblFocusActiveWindowKey = new JLabel("Focus key: "), c);
	}

	private void addTxtFocusActiveWindowKey() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		add(txtFocusActiveWindowKey = createTxtFocusActiveWindowKey(), c);
	}

	private JTextField createTxtFocusActiveWindowKey() {
		 JTextField result = new JTextField();
		 result.setEditable(false);
		 GuiUtils.setSize(result, new Dimension(150, 25));
		 result.setHorizontalAlignment(SwingConstants.CENTER);
		 return result;
	}

	private void initComponents() {
		oldValue = MyPreferences.INSTANCE.getFocusActiveWindowKey();
		initComponentsState();
		initToolTips();
		checkIsFocusActiveWindowSupported();
	}

	private void initToolTips() {
		setFocusActiveWindowKeyToolTip(
				"<html>This will be the key combination that Keepboard will send to the system to move the keyboard <br />" +
				"focus to the currently active window when an item is selected to be pasted automatically<br />" +
				"(<i>OK</i> button configuration on the main frame).<br />" +
				"<br />" +
				"This should match the keyboard shortcut configured for <i>Move focus to active or next window</i><br />" +
				"(<i>CTRL+F4</i> by default on Mac).</html>");
	}

	private void initComponentsState() {
		txtFocusActiveWindowKey.setText(oldValue.toText());
		currentKey = oldValue;
	}
	
	private void checkIsFocusActiveWindowSupported() {
		if (!RobotSupport.isRobotSupported()) {
			setFocusActiveWindowKeyComponentsEnabled(false);
			setFocusActiveWindowKeyToolTip(getRobotNotSupportedErrorMessage());
		}
	}

	private void setFocusActiveWindowKeyToolTip(String toolTip) {
		lblFocusActiveWindowKey.setToolTipText(toolTip);
		txtFocusActiveWindowKey.setToolTipText(toolTip);
	}

	private String getRobotNotSupportedErrorMessage() {
		String result = "<html>Program cannot send focus events to other applications on this platform.";
		if (Platform.isX11()) {
			result += "<br />Check whether XTEST 2.2 standard extension is supported (or enabled).";
		}
		return result + "</html>";
	}
	
	private void setFocusActiveWindowKeyComponentsEnabled(boolean enabled) {
		lblFocusActiveWindowKey.setEnabled(enabled);
		txtFocusActiveWindowKey.setEnabled(enabled);
	}

	@Override
	public void applyChanges() {
		oldValue = currentKey;
		MyPreferences.INSTANCE.setFocusActiveWindowKey(currentKey);
	}

	@Override
	public void clearChanges() {
		txtFocusActiveWindowKey.setText(oldValue.toText());
		currentKey = oldValue;
		updateChangeObervable();
	}
}
