package org.complitex.flexbuh.personnel.service;

import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.entity.ScheduleFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.07.12 15:08
 */
@Stateless
public class ScheduleBean extends TemporalDomainObjectBean<Schedule> {

    public static final String NS = ScheduleBean.class.getName();

    public ScheduleBean() {
        super(NS);
    }

    @Transactional
    @Override
    public void save(Schedule schedule) {
        if (schedule.getId() != null) {
            update(schedule);
        } else {
            create(schedule);
        }
    }

    @Transactional
    public void create(Schedule schedule) {
        schedule.setVersion(1L);
        sqlSession().insert(NS + ".insertSchedule", schedule);
    }

    @Transactional
    public void update(Schedule schedule) {
        sqlSession().update(NS + ".updateScheduleNullCompletionDate", schedule);
        sqlSession().insert(NS + ".updateSchedule", schedule);
    }

    @Transactional
    public void updateCompletionDate(Schedule schedule) {
        sqlSession().update(NS + ".updateScheduleCompletionDate", schedule);
    }

    @Override
    @NotNull
    public <A extends TemporalDomainObjectFilter> List<Schedule> getTDOObjects(@Null A f) {

        ScheduleFilter filter = (ScheduleFilter)f;
        if (filter == null) {
            filter = new ScheduleFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return sqlSession().selectList(NS + ".selectCurrentSchedules", filter);
    }

    public int getSchedulesCount(ScheduleFilter filter) {
        if (filter == null) {
            filter = new ScheduleFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return (Integer)sqlSession().selectOne(NS + ".selectCurrentSchedulesCount", filter);
    }

    @Transactional
    public void deleteSchedule(Schedule position) {
        position.setDeleted(true);
        if (position.getCompletionDate() == null) {
            position.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".delete", position);
    }
}
