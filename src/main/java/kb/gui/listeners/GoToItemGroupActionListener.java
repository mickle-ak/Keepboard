package kb.gui.listeners;

import kb.gui.ItemGroupChooser;
import kb.gui.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoToItemGroupActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent event) {
		String selectedGroup = ItemGroupChooser.showDialog("View saved items group");
		if (selectedGroup != null && !selectedGroup.isEmpty()) {
			MainFrame.getInstance().goToItemGroup(selectedGroup);
		}
	}
}