package org.complitex.flexbuh.personnel.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 29.02.12 12:09
 */
public class Allowance extends TemporalDomainObject {

    public static final List<String> CALCULATION_UNIT = Lists.newArrayList("PERCENT", "RATE");

    static {
        CALCULATION_UNIT.addAll(Payment.CURRENCY_UNIT);
    }

    // Значение
    private Float value;

    // В каких единицах изчисляется (проценты, коэффициент и т.п.)
    private String calculationUnit;

    // Тип (премия, надбавка, "северные" и т.п.)
    private String type;

    // Организация
    private Organization organization;

    // SID
    private Long sessionId;

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getCalculationUnit() {
        return calculationUnit;
    }

    public void setCalculationUnit(String calculationUnit) {
        this.calculationUnit = calculationUnit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
