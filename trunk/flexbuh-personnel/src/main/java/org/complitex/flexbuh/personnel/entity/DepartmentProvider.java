package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wickettree.ITreeProvider;

import java.util.Iterator;

/**
 * @author Pavel Sknar
 *         Date: 21.03.12 14:51
 */
public class DepartmentProvider implements ITreeProvider<Department> {

    private static final Logger log = LoggerFactory.getLogger(DepartmentProvider.class);

    private DepartmentFilter filter = new DepartmentFilter();

    private Iterator<Department> departments;

    public void setRoots(Iterator<Department> departments) {
        this.departments = departments;
    }

    @Override
    public Iterator<? extends Department> getRoots() {
        return departments;
    }

    @Override
    public boolean hasChildren(Department object) {
        return object != null && object.getChildDepartments() != null && object.getChildDepartments().size() > 0;
    }

    @Override
    public Iterator<? extends Department> getChildren(Department object) {
        return new DepartmentIterator(object.getChildDepartments());
    }

    @Override
    public IModel<Department> model(Department object) {
        return new Model<>(object);
    }

    @Override
    public void detach() {
    }

    public Long getOrganizationId() {
        return filter.getOrganizationId();
    }

    public void setOrganizationId(Long organizationId) {
        filter.setOrganizationId(organizationId);
    }

    public Long getMasterId() {
        return filter.getMasterId();
    }

    public void setMasterId(Long masterId) {
        filter.setMasterId(masterId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
