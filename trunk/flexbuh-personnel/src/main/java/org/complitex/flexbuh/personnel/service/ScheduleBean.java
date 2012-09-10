package org.complitex.flexbuh.personnel.service;

import org.apache.commons.lang.NotImplementedException;
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.07.12 15:08
 */
@Stateless
public class ScheduleBean extends TemporalDomainObjectBean<Schedule>{

    public static final String NS = ScheduleBean.class.getName();

    public ScheduleBean() {
        super(NS);
    }

    @Override
    public void save(Schedule object) {
        throw new NotImplementedException();
    }

    @Override
    public <A extends TemporalDomainObjectFilter> List<Schedule> getTDOObjects(A filter) {
        throw new NotImplementedException();
    }
}
