package common.network;

import common.model.HumanBeing;

import java.io.Serializable;
import java.util.List;

/**
 * @param collection Used for "show" command
 */
public record Response(String message, List<HumanBeing> collection) implements Serializable {
    private static final long serialVersionUID = 1L;

}