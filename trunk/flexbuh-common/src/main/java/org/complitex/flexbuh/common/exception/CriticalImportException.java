package org.complitex.flexbuh.common.exception;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.04.12 17:10
 */
public class CriticalImportException extends AbstractException {
    public CriticalImportException(Throwable cause, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
    }
}
