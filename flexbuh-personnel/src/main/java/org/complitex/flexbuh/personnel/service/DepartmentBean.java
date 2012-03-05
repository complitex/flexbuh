package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.personnel.entity.Department;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.03.12 15:49
 */
@Stateless
public class DepartmentBean extends AbstractBean {

    public static final String NS = DepartmentBean.class.getName();

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

    public Department getDepartment(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", new Date());

        return (Department) sqlSession().selectOne(NS + ".selectCurrentDepartmentById", params);
    }

    @SuppressWarnings({"unchecked"})
    public List<Department> getDepartments() {
        return sqlSession().selectList(NS + ".selectCurrentDepartments", new Date());
    }
}
