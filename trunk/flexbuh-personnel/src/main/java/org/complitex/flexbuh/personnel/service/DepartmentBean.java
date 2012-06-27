package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.DepartmentFilter;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.03.12 15:49
 */
@Stateless
public class DepartmentBean extends TemporalDomainObjectBean<Department> {

    public static final String NS = DepartmentBean.class.getName();

    public DepartmentBean() {
        super(NS);
    }

    @Transactional
    public void save(Department department) {
        if (department.getId() != null) {
            update(department);
        } else {
            create(department);
        }
    }

    @Transactional
    public void create(Department department) {
        department.setVersion(1L);
        sqlSession().insert(NS + ".insertDepartment", department);
    }

    @Transactional
    public void update(Department department) {
        sqlSession().update(NS + ".updateDepartmentNullCompletionDate", department);
        sqlSession().insert(NS + ".updateDepartment", department);
    }

    @Transactional
    public void updateCompletionDate(Department department) {
        sqlSession().update(NS + ".updateDepartmentCompletionDate", department);
    }

    @Transactional
    public void deleteDepartment(Department department) {
        department.setDeleted(true);
        if (department.getCompletionDate() == null) {
            department.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".deleteDepartment", department);
        if (department.getChildDepartments() != null) {
            for (Department childDepartment : department.getChildDepartments()) {
                childDepartment.setEntryIntoForceDate(department.getCompletionDate());
                childDepartment.setMasterDepartment(department.getMasterDepartment());
                update(childDepartment);
            }
        }
    }

    public List<Department> getDepartments(DepartmentFilter filter) {
        if (filter == null) {
            filter = new DepartmentFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return sqlSession().selectList(NS + ".selectCurrentDepartments", filter);
    }

    public int getDepartmentsCount(DepartmentFilter filter) {
        if (filter == null) {
            filter = new DepartmentFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return (Integer)sqlSession().selectOne(NS + ".selectCurrentDepartmentsCount", filter);
    }
}
