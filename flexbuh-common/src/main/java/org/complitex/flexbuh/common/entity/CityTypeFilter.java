package org.complitex.flexbuh.common.entity;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 12.01.12 9:58
 */
public class CityTypeFilter extends AbstractFilter {

    private Locale locale;
    private String startName;

    public CityTypeFilter(int first, int count, Locale locale, String startName) {
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
