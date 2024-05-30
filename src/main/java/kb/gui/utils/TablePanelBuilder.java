package kb.gui.utils;

import kb.gui.ActionsButtonBuilder;
import kb.gui.MyMouseListener;
import kb.gui.PopupMenuUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class TablePanelBuilder<M extends TableModel> {

	private final JTable table;
	private int filterColumnIndex = 0;
	
	private TablePanelPopupMenusHolder popupMenusHolder;
	private String filterFieldLabelText;
	private int filterFieldLabelMnemonic = -1;
	private int actionsButtonMnemonic = -1;
	private Component customActionsComponent;
	private List<TableCellDecorator> decorators = new ArrayList<TableCellDecorator>();
	
	public TablePanelBuilder(JTable table) {
		this.table = table;
	}
	
	private void addTableContextMenuListener() {
		table.addMouseListener(new MyMouseListener() {
			@Override
			public void contextMenuTriggered(MouseEvent e) {
				tableContextMenuTriggered(e);
			}
		});
	}
	
	private void tableContextMenuTriggered(MouseEvent e) {
		selectClickedTableRowIfNotAlreadySelected(e);
		if (hasContextMenu()) {
			displayContextMenu(e);
		}
	}

	/**
	 * Selects only clicked row if it is not already selected, otherwise
	 * does nothing - meaning that all selected rows remain selected
	 * (if we want to perform some context menu action on all selected table items).
	 */
	private void selectClickedTableRowIfNotAlreadySelected(MouseEvent e) {
		int rowToSelect = table.rowAtPoint(e.getPoint());
		if (!isRowSelected(rowToSelect)) {
			table.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
		}
	}

	private boolean isRowSelected(int row) {
		for (int selectedRow : table.getSelectedRows()) {
			if (row == selectedRow) {
				return true;
			}
		}
		return false;
	}

	private void displayContextMenu(MouseEvent e) {
		popupMenusHolder.getContextMenu().show(table, e.getX(), e.getY());
	}
	
	public TablePanel<M> build() {
		addTableContextMenuListener();
		if (hasContextMenu()) {
			PopupMenuUtils.addGlobalPopupMenuListener(popupMenusHolder.getContextMenu());
		}
		return new TablePanel<M>(table, filterColumnIndex, createFilterFieldLabel(), createActionsComponent(), decorators);
	}
	
	private Component createActionsComponent() {
		if (hasActionsMenu()) {
			return createActionsButton();
		} else if (customActionsComponent != null) {
			return customActionsComponent;
		}
		return null;
	}

	private Component createActionsButton() {
		ActionsButtonBuilder builder = new ActionsButtonBuilder(popupMenusHolder.getActionsMenu())
			.setText("Actions")
			.setSize(new Dimension(90, 25));
		
		if (hasActionsButtonMnemonic()) {
			builder.setMnemonic(actionsButtonMnemonic);
		}
		
		return builder.build();
	}
	
	private boolean hasActionsButtonMnemonic() {
		return actionsButtonMnemonic >= 0;
	}

	private boolean hasActionsMenu() {
		return popupMenusHolder != null && popupMenusHolder.getActionsMenu() != null;
	}
	
	private boolean hasContextMenu() {
		return popupMenusHolder != null && popupMenusHolder.getContextMenu() != null;
	}

	private JLabel createFilterFieldLabel() {
		if (hasFilterFieldLabelText()) {
			JLabel result = new JLabel(filterFieldLabelText + " ");
			setDisplayedMnemonic(result);
			return result;
		}
		return null;
	}

	private void setDisplayedMnemonic(JLabel label) {
		if (hasFieldLabelMnemonic()) {
			label.setDisplayedMnemonic(filterFieldLabelMnemonic);
		}
	}

	private boolean hasFieldLabelMnemonic() {
		return filterFieldLabelMnemonic >= 0;
	}

	private boolean hasFilterFieldLabelText() {
		return filterFieldLabelText != null && !filterFieldLabelText.trim().isEmpty();
	}

	public TablePanelBuilder<M> setFilterColumnIndex(int filterColumnIndex) {
		this.filterColumnIndex = filterColumnIndex;
		return this;
	}

	public TablePanelBuilder<M> setPopupMenusHolder(TablePanelPopupMenusHolder popupMenusHolder) {
		this.popupMenusHolder = popupMenusHolder;
		return this;
	}

	public TablePanelBuilder<M> setFilterFieldLabelText(String filterFieldLabelText) {
		this.filterFieldLabelText = filterFieldLabelText;
		return this;
	}

	public TablePanelBuilder<M> setFilterFieldLabelMnemonic(int textFieldLabelMnemonic) {
		this.filterFieldLabelMnemonic = textFieldLabelMnemonic;
		return this;
	}

	public TablePanelBuilder<M> setActionsButtonMnemonic(int actionsButtonMnemonic) {
		this.actionsButtonMnemonic = actionsButtonMnemonic;
		return this;
	}
	
	public TablePanelBuilder<M> setCustomActionsComponent(Component customActionsComponent) {
		this.customActionsComponent = customActionsComponent;
		return this;
	}
	
	public TablePanelBuilder<M> addTableCellDecorator(TableCellDecorator decorator) {
		this.decorators.add(decorator);
		return this;
	}
}
