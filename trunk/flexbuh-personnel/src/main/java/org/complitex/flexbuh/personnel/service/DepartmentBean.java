package org.complitex.flexbuh.personnel.service;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.DepartmentFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 01.03.12 15:49
 */
@Stateless
public class DepartmentBean extends HierarchicalTemporalDomainObjectBean<Department> {

    public static final String NS = DepartmentBean.class.getName();

    public DepartmentBean() {
        super(NS);
    }

    @Transactional
    @Override
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

    @Override
    @NotNull
    public <A extends TemporalDomainObjectFilter> List<Department> getTDOObjects(@Null A f) {

        DepartmentFilter filter = (DepartmentFilter)f;
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
