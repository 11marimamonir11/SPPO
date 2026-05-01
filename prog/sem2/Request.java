package common.network;

import common.model.HumanBeing;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final String commandStringArgument;
    private final HumanBeing objectArgument;

    public Request(String commandName, String commandStringArgument, HumanBeing objectArgument) {
        this.commandName = commandName;
        this.commandStringArgument = commandStringArgument;
        this.objectArgument = objectArgument;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandStringArgument() {
        return commandStringArgument;
    }

    public HumanBeing getObjectArgument() {
        return objectArgument;
    }
}