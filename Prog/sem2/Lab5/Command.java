package command;

// Basic command interface for the Command Pattern.

public interface Command {

    // Executes the command, @param args arguments after the command name (already split by spaces)
    void execute(String[] args);

    // @return short description for help command
    String getDescription();
}
