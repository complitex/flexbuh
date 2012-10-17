package org.complitex.flexbuh.personnel.entity;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 29.02.12 12:09
 */
public class Allowance extends TemporalDomainObject {

    private static final List<String> CALCULATION_UNIT = Lists.newArrayList("PERCENT", "RATE");
    private static final List<String> TYPE = Lists.newArrayList("");

    static {
        CALCULATION_UNIT.addAll(Payment.CURRENCY_UNIT);
    }

    // Значение
    private Float value;

    // В каких единицах изчисляется (проценты, коэффициент и т.п.)
    private String calculationUnit;

    // Тип (премия, надбавка, "северные" и т.п.)
    private String type;

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
}
