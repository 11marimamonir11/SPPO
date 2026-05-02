package common.network;

import common.model.HumanBeing;

import java.io.Serializable;

public record Request(String commandName, String commandStringArgument, HumanBeing objectArgument, String username,
                      String password) implements Serializable {
}