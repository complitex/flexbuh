package org.complitex.flexbuh.common.entity.organization;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;

/**
 * @author Pavel Sknar
 *         Date: 20.02.12 10:14
 */
public class OrganizationBase extends TemporalDomainObject implements Comparable<OrganizationBase>{

    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        StringBuilder builder = new StringBuilder();
        if (type != null) {
            builder.append(type).append(" \"");
        }
        if (name != null) {
            builder.append(name);
        }
        if (type != null) {
            builder.append("\"");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(OrganizationBase o) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getId(), o.getId());
        return builder.toComparison();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
