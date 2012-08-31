package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.DepartmentFilter;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.service.DepartmentBean;
import org.complitex.flexbuh.personnel.web.DepartmentEdit;
import org.complitex.flexbuh.personnel.web.OrganizationEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.ObjectNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 21.03.12 15:59
 */
public class DepartmentTreePanel extends TreePanel<Department> {

    private static final Logger log = LoggerFactory.getLogger(DepartmentTreePanel.class);

    private DepartmentFilter filter = new DepartmentFilter();

    private DepartmentFilter oldFilter;

    @EJB
    private DepartmentBean departmentBean;

    public DepartmentTreePanel(@NotNull String id, @NotNull Organization organization) {
        super(id, null);
        init(organization);
    }

    public DepartmentTreePanel(@NotNull String id, @NotNull Department department) {
        super(id, department);
        init(department.getOrganization());

        /*
        enabled = !department.getOrganization().isDeleted();

        filter.setOrganizationId(department.getOrganization().getId());
        filter.setEntryIntoForceDate(department.getEntryIntoForceDate());
        filter.setCompletionDate(department.getCompletionDate());
        filter.setSortProperty("name");
        filter.setCount(Integer.MAX_VALUE);
        init();*/
    }

    private void init(Organization organization) {
        enabled = !organization.isDeleted();

        filter.setOrganizationId(organization.getId());
        //filter.setEntryIntoForceDate(organization.getEntryIntoForceDate());
        //filter.setCompletionDate(organization.getCompletionDate());
        if (organization.getCompletionDate() == null) {
            filter.setCurrentDate(new Date());
        } else {
            filter.setCurrentDate(organization.getCompletionDate());
        }
        filter.setSortProperty("name");
        filter.setCount(Integer.MAX_VALUE);
        init();
    }

    @Override
    protected void deleteTDObject(Long id) throws ObjectNotFoundException {
        departmentBean.delete(id);
    }

    @Override
    protected List<Department> getHistoryTDObjects(Long id) {
        if (oldFilter == null) {
            return null;
        }
        oldFilter.setId(id);
        return departmentBean.getTDOObjects(oldFilter);
    }

    @Override
    protected List<Department> getFilteredTDObjects() {
        return departmentBean.getTDOObjects(filter);
    }

    @Override
    protected String getName(Department item) {
        return item.getName();
    }

    @Override
    protected void setAddNewItemResponsePage() {
        PageParameters parameters = new PageParameters();
        if (currentItem != null) {
            parameters.set(DepartmentEdit.PARAM_MASTER_DEPARTMENT_ID, currentItem.getId());
        } else if (filter.getOrganizationId() != null) {
            parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
        }
        setResponsePage(DepartmentEdit.class, parameters);
    }

    @Override
    protected void setEditItemResponsePage(Long id) {
        PageParameters parameters = new PageParameters();
        parameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, id);
        setResponsePage(DepartmentEdit.class, parameters);
    }

    public void updateState(Date currentDate, boolean enabled) {

        if (oldFilter == null) {
            oldFilter = new DepartmentFilter();
            oldFilter.setCount(Integer.MAX_VALUE);
        }

        oldFilter.setCurrentDate(filter.getCurrentDate());

        filter.setCurrentDate(currentDate);

        log.debug("old filter: {}\n\tnew filter: {}", oldFilter, filter);

        super.updateState(currentDate, enabled);
    }
}
