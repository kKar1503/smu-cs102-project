package parade.exceptions;

/**
 * Exception thrown when an invalid setting configuration is encountered when building the Settings
 * object.
 */
public class InvalidSettingException extends RuntimeException {
    public InvalidSettingException(String message) {
        super(message);
    }

    public InvalidSettingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSettingException(Throwable cause) {
        super(cause);
    }
}
