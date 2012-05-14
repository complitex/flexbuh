package org.complitex.flexbuh.common.exception;

import java.text.MessageFormat;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 18:53
 */
public abstract class AbstractException extends Exception{
    private boolean initial = false;

    public AbstractException(boolean initial, Throwable cause, String pattern, Object... arguments) {
        super(format(pattern, arguments), cause);
        this.initial = initial;
    }

    public AbstractException(Throwable cause, String pattern, Object... arguments) {
        super(format(pattern, arguments), cause);
    }

    public AbstractException(String pattern, Object... arguments) {
        super(format(pattern, arguments));
    }

    protected static String  format(String pattern, Object... arguments){
        try {
            return MessageFormat.format(pattern, arguments);
        } catch (Exception e) {
            return pattern;
        }
    }

    @Override
    public String getMessage() {
        if (getCause() != null){
            return super.getMessage() + ". Причина: " + (displayInitial() ? getInitialCause(this) : getCause().getMessage());
        }

        return super.getMessage();
    }

    private String getInitialCause(Throwable t){
        while (t.getCause() != null){
            t = t.getCause();
        }

        return t.getMessage();
    }

    private boolean displayInitial(){
        return initial || getCause().getMessage() == null;
    }
}
