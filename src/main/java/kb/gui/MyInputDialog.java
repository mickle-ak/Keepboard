package kb.gui;

import kb.gui.platf.PlatformDependentLayoutFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public abstract class MyInputDialog<T> extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private T value;
	private JPanel buttonsPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	protected boolean disposed;
	
	protected MyInputDialog() {
		super(MainFrame.getInstance());
		setModal(true);
		
		createComponents();
		addActions();
		setDefaultButton();
		
		MainFrame.getInstance().registerDialog(this);
	}
	
	@Override
	public void dispose() {
		this.disposed = true;
		super.dispose();
	}
	
	private void setDefaultButton() {
		getRootPane().setDefaultButton(okButton);
	}

	protected final void addDialogComponents() {
		addComponents();
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			setLocationRelativeTo(getOwner());
		}
		super.setVisible(visible);
	}
	
	protected final void clearValue() {
		value = null;
	}
	
	protected JButton getOkButton() {
		return okButton;
	}
	
	protected JButton getCancelButton() {
		return cancelButton;
	}

	private void createComponents() {
		buttonsPanel = createButtonsPanel();
	}

	private JPanel createButtonsPanel() {
		JPanel result = new JPanel();
		PlatformDependentLayoutFactory.getInstance().getOKCancelLayout(result, okButton = createOkButton(),
				cancelButton = createCancelButton()).layoutButtons();
		return result;
	}

	private JButton createOkButton() {
		JButton result = new JButton("OK");
		GuiUtils.setSize(result, new Dimension(100, 30));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				inputConfirmed();
			}
		});
		return result;
	}

	protected void inputConfirmed() {
		value = getValue();
		setVisible(false);
	}

	protected abstract T getValue();

	private JButton createCancelButton() {
		JButton result = new JButton("Cancel");
		GuiUtils.setSize(result, new Dimension(100, 30));
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		return result;
	}

	protected abstract JPanel getValuePanel();

	private void addComponents() {
		setLayout(new GridBagLayout());
		
		addValuePanel();
		addButtonsPanel();
	}

	private void addValuePanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 10, 5);
		add(getValuePanel(), c);
	}

	private void addButtonsPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 5, 5);
		add(buttonsPanel, c);
	}

	private void addActions() {
		registerEscapeKey();
	}

	private void registerEscapeKey() {
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), "myEscape");
		
		getRootPane().getActionMap().put("myEscape", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		});
	}
	
	public final T getInputValue() {
		return value;
	}
}
