package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 14:11
 */
public class OrganizationTypeFilter extends AbstractFilter {

    private String startName;

    public OrganizationTypeFilter(int first, int count, String startName) {
        super(first, count);
        this.startName = startName;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }
}
