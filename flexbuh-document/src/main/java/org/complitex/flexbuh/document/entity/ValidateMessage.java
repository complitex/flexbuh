package org.complitex.flexbuh.document.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.07.12 16:17
 */
public class ValidateMessage implements Serializable {
    private int col;
    private int row;
    private String message;

    public ValidateMessage(int col, int row, String message) {
        this.col = col;
        this.row = row;
        this.message = message;
    }

    public String getFullMessage(){
        return row + ":" + col + " " + message;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
