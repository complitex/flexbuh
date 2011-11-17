package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:04
 */
@Stateless
public class EmployeeBean extends AbstractBean{
    public Employee getEmployee(Long id){
        return (Employee) sqlSession().selectOne("selectEmployee", id);
    }

    @SuppressWarnings("unchecked")
    public List<Employee> getEmployees(EmployeeFilter filter){
        return sqlSession().selectList("selectEmployees", filter);
    }
    
    public Integer getEmployeesCount(EmployeeFilter filter){
        return (Integer) sqlSession().selectOne("selectEmployeesCount", filter);        
    }
    
    public void save(Employee employee){
        if (employee.getId() == null){
            sqlSession().insert("insertEmployee", employee);            
        }else {
            sqlSession().update("updateEmployee", employee);            
        }
    }
    
    public void delete(Long id){
        sqlSession().delete("deleteEmployee", id);
    }
}
