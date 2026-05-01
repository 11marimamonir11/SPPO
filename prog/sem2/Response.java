package common.network;

import common.model.HumanBeing;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final List<HumanBeing> collection; // Used for "show" command

    public Response(String message, List<HumanBeing> collection) {
        this.message = message;
        this.collection = collection;
    }

    public String getMessage() {
        return message;
    }

    public List<HumanBeing> getCollection() {
        return collection;
    }
}