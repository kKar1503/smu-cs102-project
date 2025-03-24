package parade.common.exceptions;

public class NetworkFailureException extends Exception {
    public NetworkFailureException(String message) {
        super(message);
    }

    public NetworkFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
