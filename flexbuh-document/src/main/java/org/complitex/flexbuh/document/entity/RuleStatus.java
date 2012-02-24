package org.complitex.flexbuh.document.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.02.12 13:57
 */
public class RuleStatus {
    private boolean checked;
    private String message;

    public RuleStatus() {
    }

    public RuleStatus(boolean checked) {
        this.checked = checked;
    }

    public RuleStatus(boolean checked, String message) {
        this.checked = checked;
        this.message = message;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
