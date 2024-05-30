package kb.gui;

import kb.ClipboardItem;
import kb.ImageClipboardItem;
import kb.PreferencesPersistor;
import kb.TextClipboardItem;
import kb.gui.prefs.MyPreferences;
import kb.gui.utils.TextAreaComponent;

import javax.swing.*;
import java.awt.*;

public class ContentPreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private TextAreaComponent textAreaComponent;
	private JLabel imageLabel;
	
	public ContentPreviewPanel() {
		setLayout(new CardLayout());
		addComponents();
		adjustLineWrapCheckBoxVisibility();
		textAreaComponent.setLineWrap(MyPreferences.INSTANCE.isPreviewAreaLineWrap());
		DialogClosingCommandsExecutor.INSTANCE.addCommand(createDialogClosingCommand());
	}
	
	private DialogClosingCommand createDialogClosingCommand() {
		return new DialogClosingCommand() {
			@Override
			public void execute() {
				executeLineWrapCommand();
			}
		};
	}
	
	private void executeLineWrapCommand() {
		boolean oldValue = MyPreferences.INSTANCE.isPreviewAreaLineWrap();
		boolean newValue = textAreaComponent.isLineWrap();
		if (oldValue != newValue) {
			MyPreferences.INSTANCE.setPreviewAreaLineWrap(newValue);
			new PreferencesPersistor().storeToDisk();
		}
	}

	public void adjustLineWrapCheckBoxVisibility() {
		PreviewAreaPosition previewAreaPosition = MyPreferences.INSTANCE.getPreviewAreaPosition();
		textAreaComponent.setLineWrapCheckBoxVisible(previewAreaPosition == PreviewAreaPosition.LEFT
				|| previewAreaPosition == PreviewAreaPosition.RIGHT);
	}

	private void addComponents() {
		textAreaComponent = createTextAreaComponent();
		imageLabel = createImageLabel();
		
		add(imageLabel, "image");
		add(textAreaComponent.getComponent(), "text");
	}
	
	private JLabel createImageLabel() {
		JLabel result = new JLabel();
		result.setHorizontalAlignment(SwingConstants.CENTER);
		return result;
	}

	private TextAreaComponent createTextAreaComponent() {
		TextAreaComponent result = new TextAreaComponent();
		result.getTextArea().setEditable(false);
		return result;
	}
	
	public void setContent(ClipboardItem clipboardItem) {
		if (clipboardItem == null) {
			setEmptyContent();
		} else if (clipboardItem instanceof TextClipboardItem) {
			setTextContent((TextClipboardItem) clipboardItem);
		} else {
			setImageContent((ImageClipboardItem) clipboardItem);
		}
	}

	private void setEmptyContent() {
		((CardLayout) getLayout()).show(this, "image");
		imageLabel.setIcon(null);
	}

	private void setTextContent(TextClipboardItem clipboardItem) {
		((CardLayout) getLayout()).show(this, "text");
		textAreaComponent.setText(clipboardItem.getShortText());
		if (clipboardItem.getFileName() != null) {
			textAreaComponent.getTextArea().append("\n <more text>...");
		}
		textAreaComponent.getTextArea().setCaretPosition(0);
	}
	
	private void setImageContent(ImageClipboardItem clipboardItem) {
		((CardLayout) getLayout()).show(this, "image");
		imageLabel.setIcon(new ImageIcon(clipboardItem.getPreviewImage()));
	}
}
