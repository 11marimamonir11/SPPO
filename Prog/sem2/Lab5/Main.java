import app.CommandRegistry;
import command.impl.*;
import manager.CollectionManager;
import util.InputManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No file name provided!");
            return;
        }

        //  Create CollectionManager and load file
        CollectionManager cm = new CollectionManager(args[0]);
        cm.loadFromFile();

        // Create registry
        CommandRegistry registry = new CommandRegistry();

        // Create ONE Scanner for entire program
        Scanner scanner = new Scanner(System.in);

        // Create InputManager using that scanner
        InputManager input = new InputManager(scanner);

        // Register all commands
        registry.register("help", new HelpCommand(registry));
        registry.register("info", new InfoCommand(cm));
        registry.register("show", new ShowCommand(cm));
        registry.register("save", new SaveCommand(cm));
        registry.register("clear", new ClearCommand(cm));
        registry.register("remove_by_id", new RemoveByIdCommand(cm));
        registry.register("add", new AddCommand(cm, input));
        registry.register("update", new UpdateCommand(cm, input));
        registry.register("execute_script", new ExecuteScriptCommand(registry, input));
        registry.register("print_descending", new PrintDescendingCommand(cm));
        registry.register("shuffle", new ShuffleCommand(cm));
        registry.register("add_if_min", new AddIfMinCommand(cm, input));
        registry.register("remove_greater", new RemoveGreaterCommand(cm, input));
        registry.register("filter_by_impact_speed", new FilterByImpactSpeedCommand(cm));
        registry.register("remove_all_by_minutes_of_waiting", new RemoveAllByMinutesOfWaitingCommand(cm));
        registry.register("exit", new ExitCommand());

        // Interactive loop
        System.out.println("Interactive mode started. Type 'help'.");

        while (true) {
            System.out.print("> ");
            String inputLine = scanner.nextLine().trim();

            if (inputLine.isEmpty()) continue;

            String[] parts = inputLine.split(" ");
            String commandName = parts[0];

            String[] commandArgs = new String[parts.length - 1];
            System.arraycopy(parts, 1, commandArgs, 0, commandArgs.length);

            boolean executed = registry.executeCommand(commandName, commandArgs);

            if (!executed) {
                System.out.println("Unknown command. Type 'help'.");
            }
        }
    }
}

