package kb.gui;

import kb.PreferencesPersistor;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DividerLocationCalculator {
	
	private final JSplitPane splitPane;
	
	public DividerLocationCalculator(JSplitPane splitPane) {
		this.splitPane = splitPane;
	}
	
	public void addActions() {
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, createSplitPaneDividerLocationPropertyListener());
	}

	private PropertyChangeListener createSplitPaneDividerLocationPropertyListener() {
		return new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent changeEvent) {
				JSplitPane splitPane = (JSplitPane) changeEvent.getSource();
				
				boolean itemPreviewHiddenOld = MyPreferences.INSTANCE.isItemPreviewHidden();
				boolean itemPreviewHiddenNew = calcItemPreviewHiddenPrefValue(splitPane);
				
				if (itemPreviewHiddenOld != itemPreviewHiddenNew) {
					MyPreferences.INSTANCE.setItemPreviewHidden(itemPreviewHiddenNew);
					new PreferencesPersistor().storeToDisk();
				}
			}
		};
	}
	
	private boolean calcItemPreviewHiddenPrefValue(JSplitPane splitPane) {
		PreviewAreaPosition previewAreaPosition = getPreviewAreaPositionPref();
		if (previewAreaPosition == PreviewAreaPosition.TOP || previewAreaPosition == PreviewAreaPosition.LEFT) {
			return splitPane.getDividerSize() + splitPane.getDividerLocation() < 5;
		} else if (previewAreaPosition == PreviewAreaPosition.BOTTOM) {
			return splitPane.getHeight() - splitPane.getDividerSize() - splitPane.getDividerLocation() < 5;
		} else {
			return splitPane.getWidth() - splitPane.getDividerSize() - splitPane.getDividerLocation() < 5;
		}
	}
	
	public void initDividerLocation() {
		int dividerLocation = getDividerLocationPref();
		if (dividerLocation == 0 && getPreviewAreaPositionPref() == PreviewAreaPosition.BOTTOM) {
			setSplitPaneDividerLocation(calculateDefaultDividerLocationForBottomPreviewArea());
		} else {
			setSplitPaneDividerLocation(dividerLocation);
		}
	}
	
	public void setDividerLocation() {
		setSplitPaneDividerLocation(getDividerLocationPref());
	}
	
	private void setSplitPaneDividerLocation(int dividerLocation) {
		splitPane.setDividerLocation(dividerLocation);
		if (MyPreferences.INSTANCE.isItemPreviewHidden()) {
			PreviewAreaPosition previewAreaPosition = getPreviewAreaPositionPref();
			if (previewAreaPosition == PreviewAreaPosition.TOP || previewAreaPosition == PreviewAreaPosition.LEFT) {
				splitPane.setDividerLocation(0);
			} else {
				splitPane.setDividerLocation(Integer.MAX_VALUE);
			}
		}
		storeDividerLocationIfChanged(dividerLocation);
	}

	private void storeDividerLocationIfChanged(int dividerLocation) {
		if (getDividerLocationPref() != dividerLocation) {
			MyPreferences.INSTANCE.setDividerLocation(dividerLocation);
			new PreferencesPersistor().storeToDisk();
		}
	}

	private int calculateDefaultDividerLocationForBottomPreviewArea() {
		int result = MyPreferences.INSTANCE.getMainFrameSize().getHeight() - 223;
		
		if (MyPreferences.INSTANCE.areButtonsHidden()) {
			result += 35;
		}
		
		if (MyPreferences.INSTANCE.isToolbarHidden()) {
			result += 23;
		}
		
		return result;
	}
	
	public void setDividerLocationAfterButtonsVisibilityChange(boolean buttonsVisible) {
		if (getPreviewAreaPositionPref() == PreviewAreaPosition.BOTTOM) {
			setSplitPaneDividerLocation(getDividerLocationPref() + (buttonsVisible ? -35 : 35));
		}
	}
	
	public void setDividerLocationAfterToolbarVisibilityChange(boolean toolbarVisible) {
		if (getPreviewAreaPositionPref() == PreviewAreaPosition.BOTTOM) {
			setSplitPaneDividerLocation(getDividerLocationPref() + (toolbarVisible ? -23 : 23));
		}
	}

	private int getDividerLocationPref() {
		return MyPreferences.INSTANCE.getDividerLocation();
	}
	
	private PreviewAreaPosition getPreviewAreaPositionPref() {
		return MyPreferences.INSTANCE.getPreviewAreaPosition();
	}

	public void setDividerLocationAfterPreviewAreaPositionChange() {
		PreviewAreaPosition previewAreaPosition = getPreviewAreaPositionPref();
		
		if (previewAreaPosition == PreviewAreaPosition.TOP) {
			setSplitPaneDividerLocation(90);
		} else if (previewAreaPosition == PreviewAreaPosition.BOTTOM) {
			setSplitPaneDividerLocation(calculateDefaultDividerLocationForBottomPreviewArea());
		} else if (previewAreaPosition == PreviewAreaPosition.LEFT) {
			setSplitPaneDividerLocation(400);
		} else {
			setSplitPaneDividerLocation(MainFrame.getInstance().getWidth() - 400);
		}
	}
}