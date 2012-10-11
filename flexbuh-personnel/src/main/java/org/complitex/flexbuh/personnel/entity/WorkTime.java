package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.deser.std.CalendarDeserializer;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 10.10.12 18:00
 */
public class WorkTime implements Serializable {
    private Date beginTime;
    private Date endTime;

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean checkTime() {
        return beginTime != null && endTime != null && beginTime.before(endTime);
    }

    public boolean isEmpty() {
        if (beginTime == null || endTime == null) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginTime);
        boolean result = calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0;
        calendar.setTime(endTime);
        return result && calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
