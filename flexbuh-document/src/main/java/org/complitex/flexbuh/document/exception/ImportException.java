package org.complitex.flexbuh.document.exception;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.03.12 15:07
 */
public class ImportException extends Exception {
    public ImportException(Throwable cause) {
        super(cause);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
