package org.complitex.flexbuh.common.entity.organization;

import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 14:11
 */
public class OrganizationTypeFilter extends AbstractFilter {

    private Locale locale;
    private String startName;

    public OrganizationTypeFilter(int first, int count, Locale locale, String startName) {
        super(first, count);
        this.locale = locale;
        this.startName = startName;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }
}
