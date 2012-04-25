package org.complitex.flexbuh.document.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.12.11 14:27
 */
public class Period implements Serializable, Comparable<Period>{
    // order = period_month + 13*period_type
    private final static List<Integer> ORDER = Arrays.asList(1+13, 2+13, 3+13, 3 + 13*2, 4+13, 5+13, 6+13, 6 + 13*2,
            6 + 13*3, 7+13, 8+13, 9+13, 9 + 13*2, 9 + 13*4, 10+13, 11+13, 12+13, 12 + 13*2, 12 + 13*3, 12 + 13*5);

    private int month;
    private int type;
    private int year;

    public Period() {
    }

    public Period(int month, int type, int year) {
        this.month = month;
        this.type = type;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Period)) return false;

        Period period = (Period) o;

        if (month != period.month) return false;
        if (type != period.type) return false;
        if (year != period.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + type;
        result = 31 * result + year;
        return result;
    }

    @Override
    public int compareTo(Period p) {
        if (p != null){
            if (year == p.year){
                return new Integer(ORDER.indexOf(month + 13*type)).compareTo(ORDER.indexOf(p.month + 13*p.type));
            }else {
                return Integer.compare(year, p.year);
            }
        }

        return 0;
    }
}
