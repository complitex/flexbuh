package org.complitex.flexbuh.personnel.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.OrganizationFilter;
import org.complitex.flexbuh.personnel.service.DepartmentBean;
import org.complitex.flexbuh.personnel.service.OrganizationBean;
import org.slf4j.Logger;

import javax.ejb.EJB;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 05.03.12 11:09
 */
public class DepartmentEntityTestPage extends WebPage {

    private Logger log = org.slf4j.LoggerFactory.getLogger(DepartmentEntityTestPage.class);

    @EJB
    private DepartmentBean departmentBean;

    @EJB
    private OrganizationBean organizationBean;

    public DepartmentEntityTestPage() {
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                save();
            }
        });
    }

    private void save() {

        Organization organization = new Organization();
        organization.setName("test organization");
        organization.setEmail("test@test.ru");
        organization.setEntryIntoForceDate(new Date());
        organizationBean.create(organization, getLocale());

        Department department = new Department();
        department.setOrganization(organization);
        department.setEntryIntoForceDate(new Date());
        department.setName("test department 1");
        departmentBean.create(department);
        log.debug("create department: {}", department);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        department.setName("test department 2");
        department.setEntryIntoForceDate(new Date());
        departmentBean.update(department);
        log.debug("update department: {}", department);

        log.debug("Select department by id: {}", departmentBean.getDepartment(department.getId()));
        log.debug("Select all departments: {}", departmentBean.getDepartments(null));

        OrganizationFilter filter = new OrganizationFilter();
        filter.setPhysicalAddress("Kharkov");
        log.debug("Select organizations: {}", organizationBean.getOrganizations(filter));
    }

}
