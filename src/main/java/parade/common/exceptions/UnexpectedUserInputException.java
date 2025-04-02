package parade.common.exceptions;

public class UnexpectedUserInputException extends RuntimeException {
    public UnexpectedUserInputException() {}

    public UnexpectedUserInputException(String message) {
        super(message);
    }
}
