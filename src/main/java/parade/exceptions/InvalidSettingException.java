package parade.exceptions;

public class InvalidSettingException extends RuntimeException {
    public InvalidSettingException(String message) {
        super(message);
    }

    public InvalidSettingException(String message, Throwable cause) {
        super(message, cause);
    }
}
