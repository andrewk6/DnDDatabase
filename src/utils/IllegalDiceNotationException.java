package utils;

public class IllegalDiceNotationException extends RuntimeException {
    public IllegalDiceNotationException() {
        super("Invalid dice notation.");
    }

    public IllegalDiceNotationException(String message) {
        super(message);
    }

    public IllegalDiceNotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDiceNotationException(Throwable cause) {
        super(cause);
    }
}
