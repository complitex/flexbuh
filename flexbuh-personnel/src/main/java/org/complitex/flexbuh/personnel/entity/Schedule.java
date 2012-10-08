package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.common.entity.TemporalDomainObject;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 14:38
 */
public class Schedule extends TemporalDomainObject {
    // Название графика
    private String name;

    // Периодичность, т.е. количество дней в периоде (пятидневная рабочая неделя с 2-мя выходными = 7 дней)
    private Long periodNumberDate;

    // Номера выходных дней в периоде через запятую (для 5-ти дневной рабочей недели: 6, 7)
    private String itemDayOff;

    // Единицы учета рабочего времени (дни/часы)
    private String regWorkTimeUnit;

    // Перечисление через точку с запятой графика работы на каждый рабочий день в порядке возрастания
    // [H[:m]-H[:m][,H[:m]-H[:m]]*][;H[:m]-H[:m][,H[:m]-H[:m]]*]*
    private String periodSchedule;

    // Годовой календарь [Тип_календаря#[Календарь]]
    // Календарь может быть 2-х типов
    // 1. Перечисление через точку с запятой календарных дней, отклоняющихся от основного скользящего графика.
    // Этот тип характерен малым изменениям основного скользящего графика.
    // [d:M:yyyy H[:m]-H[:m][,H[:m]-H[:m]]*;]*
    // 2. Перечисление через точку с запятой всего календарного года без указания даты.
    // [H[:m]-H[:m][,H[:m]-H[:m]]*][;H[:m]-H[:m][,H[:m]-H[:m]]*]{364,365}
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPeriodNumberDate() {
        return periodNumberDate;
    }

    public void setPeriodNumberDate(Long periodNumberDate) {
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
}
