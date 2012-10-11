package org.complitex.flexbuh.personnel.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:38
 */
public class Schedule extends TemporalDomainObject {

    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    // Название графика
    private String name;

    // Периодичность, т.е. количество дней в периоде (пятидневная рабочая неделя с 2-мя выходными = 7 дней)
    private Integer periodNumberDate;

    // Номера выходных дней в периоде через запятую (для 5-ти дневной рабочей недели: 6, 7)
    private String itemDayOff;

    // Единицы учета рабочего времени (дни/часы)
    private String regWorkTimeUnit;

    // Перечисление через точку с запятой графика работы на каждый день в порядке возрастания
    // [H[:m]-H[:m][,H[:m]-H[:m]]*][;H[:m]-H[:m][,H[:m]-H[:m]]*]*
    private String periodSchedule;

    // Годовой календарь [Тип_календаря#[Календарь]]
    // Календарь может быть 2-х типов
    // 1. Перечисление через точку с запятой календарных дней, отклоняющихся от основного скользящего графика.
    // Этот тип характерен малым изменениям основного скользящего графика.
    // [d:M:yyyy H[:m]-H[:m][,H[:m]-H[:m]]*;]*
    // 2. Перечисление через точку с запятой всего календарного года без указания даты.
    // [H[:m]-H[:m][,H[:m]-H[:m]]*][;H[:m]-H[:m][,H[:m]-H[:m]]*]{365,366}
    private String yearSchedule;

    // Шаблон
    private boolean pattern;

    // Суммированный учет рабочего времени
    private boolean totalWorkTime;

    // Организация
    private Organization organization;

    // Комментарий
    private String comment;

    // SID
    private Long sessionId;

    private List<List<WorkTime>> periodScheduleTime = Lists.newArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeriodNumberDate() {
        return periodNumberDate;
    }

    public void setPeriodNumberDate(Integer periodNumberDate) {
        this.periodNumberDate = periodNumberDate;
    }

    public String getItemDayOff() {
        return itemDayOff;
    }

    public void setItemDayOff(String itemDayOff) {
        this.itemDayOff = itemDayOff;
    }

    public String getRegWorkTimeUnit() {
        return regWorkTimeUnit;
    }

    public void setRegWorkTimeUnit(String regWorkTimeUnit) {
        this.regWorkTimeUnit = regWorkTimeUnit;
    }

    public String getPeriodSchedule() {
        return periodSchedule;
    }

    public void setPeriodSchedule(String periodSchedule) {
        this.periodSchedule = periodSchedule;
        this.periodScheduleTime = parseScheduleDate(periodSchedule);
    }

    public List<List<WorkTime>> getPeriodScheduleTime() {
        return periodScheduleTime;
    }

    public void setPeriodScheduleTime(List<List<WorkTime>> periodScheduleTime) {
        this.periodScheduleTime = periodScheduleTime;
        this.periodSchedule = toStringScheduleWorkTime(periodScheduleTime);
        this.itemDayOff = evaluateItemDayOff(periodScheduleTime);
    }

    public String getYearSchedule() {
        return yearSchedule;
    }

    public void setYearSchedule(String yearSchedule) {
        this.yearSchedule = yearSchedule;
    }

    public boolean isTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(boolean totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public boolean isPattern() {
        return pattern;
    }

    public void setPattern(boolean pattern) {
        this.pattern = pattern;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void synchronizeUpdatedObject() {
        periodSchedule = toStringScheduleWorkTime(periodScheduleTime);
        itemDayOff = evaluateItemDayOff(periodScheduleTime);
    }

    private String toStringScheduleWorkTime(List<List<WorkTime>> scheduleTime) {
        Calendar calendar = Calendar.getInstance();
        StringBuilder result = new StringBuilder();

        boolean notFirst = false;
        for (List<WorkTime> workTimeList : scheduleTime) {
            if (notFirst) {
                result.append(";");
            } else {
                notFirst = true;
            }
            boolean notFirst2 = false;
            for (WorkTime workTime : workTimeList) {
                if (workTime.isEmpty()) {
                    continue;
                }
                if (notFirst2) {
                    result.append(",");
                } else {
                    notFirst2 = true;
                }
                calendar.setTime(workTime.getBeginTime());
                appendTime(result, calendar).append("-");
                calendar.setTime(workTime.getEndTime());
                appendTime(result, calendar);
            }
        }
        return result.toString();
    }

    private String evaluateItemDayOff(List<List<WorkTime>> scheduleTime) {
        StringBuilder itemDayOff = new StringBuilder();

        int numberDay = 1;
        for (List<WorkTime> workTimeList : scheduleTime) {

            boolean empty = true;
            for (WorkTime workTime : workTimeList) {
                if (workTime.isEmpty()) {
                    continue;
                }
                empty = false;
            }
            if (empty) {
                if (itemDayOff.length() > 0) {
                    itemDayOff.append(",");
                }
                itemDayOff.append(numberDay);
            }
            numberDay++;
        }
        return itemDayOff.toString();
    }

    private List<List<WorkTime>> parseScheduleDate(String inputString) {
        List<List<WorkTime>> result = Lists.newArrayList();
        for (String workTimes : inputString.split(";")) {
            List<WorkTime> workTimeList = Lists.newArrayList();
            for (String workTime : workTimes.split(",")) {
                String[] times = workTime.split("-");
                WorkTime item = new WorkTime();
                item.setBeginTime(getTime(times[0]));
                item.setEndTime(getTime(times[1]));
                workTimeList.add(item);
            }
            result.add(workTimeList);
        }
        return result;
    }

    private StringBuilder appendTime(StringBuilder builder, Calendar time) {
        builder.append(time.get(Calendar.HOUR_OF_DAY));
        int minute = time.get(Calendar.MINUTE);
        if (minute > 0) {
            builder.append(":").append(minute);
        }
        return builder;
    }

    private Date getTime(String sTime) {
        String[] parsedTime = sTime.split(":");
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(parsedTime[0]));
        c.set(Calendar.MINUTE, parsedTime.length > 1? Integer.valueOf(parsedTime[0]): 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
