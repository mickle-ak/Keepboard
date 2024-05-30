package kb.gui;

import java.util.ArrayList;
import java.util.List;

public class DialogClosingCommandsExecutor {
	
	public static DialogClosingCommandsExecutor INSTANCE = new DialogClosingCommandsExecutor();
	
	private List<DialogClosingCommand> commands = new ArrayList<DialogClosingCommand>();
	
	private DialogClosingCommandsExecutor() {
		// private constructor
	}
	
	public void executeCommands() {
		for (DialogClosingCommand command : commands) {
			command.execute();
		}
	}
	
	public void addCommand(DialogClosingCommand command) {
		commands.add(command);
	}
	
	public void removeAllCommands() {
		commands.clear();
	}
}
