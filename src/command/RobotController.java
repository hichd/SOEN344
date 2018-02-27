package command;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

//plays the role of the Invoker in the Command design pattern
public class RobotController {
	private LinkedList<RobotCommand> commands;
	private Stack<RobotCommand> undoStack;
	
	public RobotController() {
		this.commands = new LinkedList<RobotCommand>();
		this.undoStack = new Stack<RobotCommand>();
	}

	public void addCommand(RobotCommand command) {
		commands.add(command);
	}

	public void executeCommands() {
		/*Iterator<RobotCommand> iterator = commands.iterator();
		while(iterator.hasNext()) {
			RobotCommand command = iterator.next();
			command.execute();
			iterator.remove();
			undoStack.add(command);
		}*/
		
		while(commands.size() > 0) {
			RobotCommand command = commands.removeFirst();
			command.execute();
			undoStack.add(command);
		}
	}

	public void undoCommands(int numUndos) {
		int endOfLoop = (numUndos <= undoStack.size()) ? numUndos : undoStack.size();
		for(int i=0; i < endOfLoop; i++) {
			undoStack.pop().undo();
		}
	}
}
