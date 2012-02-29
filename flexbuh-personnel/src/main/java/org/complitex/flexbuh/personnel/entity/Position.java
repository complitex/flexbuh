package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:39
 */
public class Position extends AbstractTemporalEntity{

    // Название должности (специальности, профессии), разряда, класса (категории) квалификации
    private String name;

    // Код должности
    private String code;

    // Система оплаты труда
    private Payment payment;

    // Описание (должностные обязонности и т.п.)
    private String description;

    // Департамент
    private Department department;

    // Непосредственный руководитель
    private Position masterPosition;

    // Подчиненные
    private List<Position> childPositions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getMasterPosition() {
        return masterPosition;
    }

    public void setMasterPosition(Position masterPosition) {
        this.masterPosition = masterPosition;
    }

    public List<Position> getChildPositions() {
        return childPositions;
    }

    public void setChildPositions(List<Position> childPositions) {
        this.childPositions = childPositions;
    }
}
