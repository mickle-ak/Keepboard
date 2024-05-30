package kb.gui.utils;

import kb.utils.Observable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


public class ErrorAwareTextFieldBuilder {
	
	private JTextField textField;
	private TextErrorChecker errorChecker;
	private Observable<Boolean> changeObservable = new Observable<Boolean>();
	
	private Color textFieldBackground;
	
	private String correctStateTooltip;
	private String errorStateTooltip;
	
	public ErrorAwareTextFieldBuilder(JTextField textField, TextErrorChecker errorChecker) {
		this.textField = textField;
		this.errorChecker = errorChecker;
		this.textFieldBackground = textField.getBackground();
		addDocumentListener();
	}

	private void addDocumentListener() {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				textChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				textChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Not needed
			}
		});
	}

	private void textChanged() {
		adjustTextField();
		changeObservable.updateObservers(!errorChecker.isError(textField.getText()));
	}

	private void adjustTextField() {
		boolean error = errorChecker.isError(textField.getText());
		textField.setBackground(error ? Color.red : textFieldBackground);
		textField.setToolTipText(error ? errorStateTooltip : correctStateTooltip);
	}

	public Observable<Boolean> getChangeObservable() {
		return changeObservable;
	}

	public void setCorrectStateTooltip(String correctStateTooltip) {
		this.correctStateTooltip = correctStateTooltip;
	}

	public void setErrorStateTooltip(String errorStateTooltip) {
		this.errorStateTooltip = errorStateTooltip;
	}
}
