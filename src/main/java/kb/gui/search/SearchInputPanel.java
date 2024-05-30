package kb.gui.search;

import kb.gui.GuiUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class SearchInputPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextField txtContainingText;
	private JCheckBox chkCaseSensitive;
	private JButton btnSearch;
	private JLabel lblStatus;
	
	private SearchPanel searchPanel;
	
	private boolean searching = false;
	
	private String lastSearchedText;
	
	public SearchInputPanel() {
		createComponents();
		addComponents();
		addActions();
	}
	
	private void addActions() {
		txtContainingText.addFocusListener(createTxtContainingTextFocusListener());
		txtContainingText.getDocument().addDocumentListener(createTxtContainingTextDocumentListener());
		addTxtContainingTextEnterAction();
	}
	
	public void setSearchPanel(SearchPanel searchPanel) {
		this.searchPanel = searchPanel;
		searchPanel.getSearchResultsPanel().addNavigationListeners(txtContainingText);
	}
	
	private void addTxtContainingTextEnterAction() {
		txtContainingText.getInputMap(JRootPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ENTER, 0), "myEnter");
		txtContainingText.getActionMap().put("myEnter", createEnterAction(false));

		txtContainingText.getInputMap(JRootPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK), "myShiftEnter");
		txtContainingText.getActionMap().put("myShiftEnter", createEnterAction(true));
	}
	
	private AbstractAction createEnterAction(final boolean onlyCopyToClipboard) {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				txtContainingTextEnterPressed(onlyCopyToClipboard);
			}
		};
	}
	
	private void txtContainingTextEnterPressed(boolean onlyCopyToClipboard) {
		String searchedText = txtContainingText.getText();
		
		if (searchedText.equals(lastSearchedText)) {
			searchPanel.getSearchResultsPanel().getTablePanel().selectionApproved(onlyCopyToClipboard);
		} else if (!searching && !searchedText.isEmpty()) {
			lastSearchedText = searchedText;
			startSearch();
			searching = true;
		}
	}

	private FocusListener createTxtContainingTextFocusListener() {
		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				txtContainingText.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtContainingText.select(0, 0);
			}
		};
	}

	private DocumentListener createTxtContainingTextDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkBtnSearchEnabledState();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				checkBtnSearchEnabledState();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Not needed
			}
		};
	}

	private void checkBtnSearchEnabledState() {
		btnSearch.setEnabled(!txtContainingText.getText().isEmpty());
	}

	private void createComponents() {
		createTxtContainingText();
		createChkCaseSensitive();
		lblStatus = new JLabel();
		createBtnSearch();
	}

	private void createTxtContainingText() {
		txtContainingText = new JTextField();
		GuiUtils.setSize(txtContainingText, new Dimension(50, 25));
	}

	private void createChkCaseSensitive() {
		chkCaseSensitive = new JCheckBox("Case sensitive");
		chkCaseSensitive.setMnemonic(KeyEvent.VK_A);
	}

	private void createBtnSearch() {
		btnSearch = new JButton("Search");
		GuiUtils.setSize(btnSearch, new Dimension(85, 30));
		
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSearchPressed();
			}
		});
		
		btnSearch.setEnabled(false);
	}

	private void btnSearchPressed() {
		if (searching) {
			searchPanel.stopSearch();
			btnSearch.setText("Search");
			lblStatus.setText("Searching stopped");
		} else {
			startSearch();
		}
		
		searching = !searching;
	}

	private void startSearch() {
		searchPanel.startSearch(txtContainingText.getText(), chkCaseSensitive.isSelected());
		btnSearch.setText("Stop");
		lblStatus.setText("Searching...");
	}
	
	public void searchingFinished(int itemsFound) {
		searching = false;
		btnSearch.setText("Search");
		lblStatus.setText(itemsFound + (itemsFound == 1 ? " item" : " items") + " found");
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addContainingTextLabel();
		addTxtContainingText();
		addBtnSearch();
		addLblStatus();
		addChkCaseSensitive();
	}

	private void addContainingTextLabel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.insets = new Insets(2, 0, 2, 0);
		add(createTxtContainingTextLabel(), c);
	}
	
	private JLabel createTxtContainingTextLabel() {
		JLabel result = new JLabel("Containing text: ");
		result.setLabelFor(txtContainingText);
		result.setDisplayedMnemonic(KeyEvent.VK_X);
		return result;
	}

	private void addTxtContainingText() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 0, 2, 0);
		add(txtContainingText, c);
	}
	
	private void addBtnSearch() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 0, 0);
		add(btnSearch, c);
	}
	
	private void addLblStatus() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 3, 0, 0);
		add(lblStatus, c);
	}

	private void addChkCaseSensitive() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 4);
		c.anchor = GridBagConstraints.EAST;
		add(chkCaseSensitive, c);
	}

	public void focusTextField() {
		txtContainingText.requestFocusInWindow();
	}
	
	public JTextField getTxtContainingText() {
		return txtContainingText;
	}
}
