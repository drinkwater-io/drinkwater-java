package next.hosts.exceptions;

/**
 * Created by A406775 on 24/03/2017.
 */
public class NextApplicationConfigurationException extends Exception {
    public NextApplicationConfigurationException() {
    }

    public NextApplicationConfigurationException(String message) {
        super(message);
    }

    public NextApplicationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NextApplicationConfigurationException(Throwable cause) {
        super(cause);
    }

    public NextApplicationConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
