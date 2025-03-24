package parade.common.exceptions;

public class PlayerControllerInitialisationException extends Exception {
    public PlayerControllerInitialisationException(String message) {
        super(message);
    }

    public PlayerControllerInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }
}
