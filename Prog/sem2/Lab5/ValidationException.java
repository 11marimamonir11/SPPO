package exception;

// Thrown when a field violates the rules from the assignment.

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
