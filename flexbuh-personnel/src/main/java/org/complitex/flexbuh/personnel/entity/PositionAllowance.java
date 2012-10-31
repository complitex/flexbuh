package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 31.10.12 14:33
 */
public class PositionAllowance extends TemporalDomainObject {

    private Position position;

    private Allowance allowance;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Allowance getAllowance() {
        return allowance;
    }

    public void setAllowance(Allowance allowance) {
        this.allowance = allowance;
    }
}
