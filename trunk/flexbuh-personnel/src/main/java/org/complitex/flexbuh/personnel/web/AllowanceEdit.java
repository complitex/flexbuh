package org.complitex.flexbuh.personnel.web;

import org.apache.commons.lang.NotImplementedException;
import org.complitex.flexbuh.personnel.entity.Schedule;
import org.complitex.flexbuh.personnel.web.component.HistoryPanelFactory;
import org.complitex.flexbuh.personnel.web.component.TemporalObjectEdit;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 16:31
 */
public class AllowanceEdit extends TemporalObjectEdit<Schedule> {
    public static final String PARAM_ALLOWANCE_ID = "allowance_id";

    @Override
    protected HistoryPanelFactory<Schedule> getHistoryPanelFactory() {
        throw new NotImplementedException();
    }
}
