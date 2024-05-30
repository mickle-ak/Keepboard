package kb.gui.listeners;

import kb.gui.MainFrame;
import kb.gui.PreviewAreaPosition;
import kb.gui.prefs.MyPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreviewAreaPositionActionListener implements ActionListener {

	private final PreviewAreaPosition previewAreaPosition;
	
	public PreviewAreaPositionActionListener(PreviewAreaPosition previewAreaPosition) {
		this.previewAreaPosition = previewAreaPosition;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (MyPreferences.INSTANCE.getPreviewAreaPosition() != previewAreaPosition) {
			MyPreferences.INSTANCE.setPreviewAreaPosition(previewAreaPosition);
			MainFrame.getInstance().setPreviewAreaPosition(previewAreaPosition);
		}
	}
}
