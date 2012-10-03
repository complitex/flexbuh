package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.personnel.entity.Position;
import org.complitex.flexbuh.personnel.service.TemporalDomainObjectBean;
import org.complitex.flexbuh.personnel.web.component.TemporalDomainObjectUpdate;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEdit;

/**
 * @author Pavel Sknar
 *         Date: 03.10.12 11:19
 */

@AuthorizeInstantiation(SecurityRole.PERSONAL_MANAGER)
public class ScheduleEdit extends TemporalObjectEdit<Position> {
    public static final String PARAM_SCHEDULE_ID = "schedule_id";

    @Override
    protected Position getTDObject() {
        throw new NotImplementedException();
    }

    @Override
    protected Position getOldTDObject() {
        throw new NotImplementedException();
    }

    @Override
    protected TemporalDomainObjectUpdate<Position> getTDObjectUpdate() {
        throw new NotImplementedException();
    }

    @Override
    protected TemporalDomainObjectBean<Position> getTDObjectBean() {
        throw new NotImplementedException();
    }
}
