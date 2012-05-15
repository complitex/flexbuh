package org.complitex.flexbuh.common.exception;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.05.12 15:53
 */
public class CauseMessageException extends AbstractException {
    public CauseMessageException(boolean initial, Throwable cause, String pattern, Object... arguments) {
        super(initial, cause, pattern, arguments);
    }

    public CauseMessageException(Throwable cause, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
    }

    public CauseMessageException(Throwable cause) {
        super(cause, cause.getMessage());
    }

    public CauseMessageException(String pattern, Object... arguments) {
        super(pattern, arguments);
    }
}
