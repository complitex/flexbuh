package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:04
 */
@Stateless
public class EmployeeBean extends AbstractBean{
    private final static Logger log = LoggerFactory.getLogger(CounterpartBean.class);
    
    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private FIOBean fioBean;

    public Employee getEmployee(Long id){
        return (Employee) sqlSession().selectOne("selectEmployee", id);
    }

    public List<Employee> getEmployees(FilterWrapper<Employee> filter){
        return sqlSession().selectList("selectEmployees", filter);
    }

    public Integer getEmployeesCount(FilterWrapper<Employee> filter){
        return (Integer) sqlSession().selectOne("selectEmployeesCount", filter);
    }

    /**
     * @param sessionId Идентификатор сессии
     * @return Список всех сотрудников в данной сессии
     */
    public List<Employee> getAllEmployees(Long sessionId){
        return sqlSession().selectList("selectAllEmployees", sessionId);
    }

    public void save(Employee employee){
        fioBean.createFIO(employee.getFirstName(), employee.getMiddleName(), employee.getLastName());

        if (employee.getId() == null){
            sqlSession().insert("insertEmployee", employee);
        }else {
            sqlSession().update("updateEmployee", employee);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteEmployee", id);
    }

    public int save(Long sessionId, Long personProfileId, InputStream inputStream) {
        try {
            EmployeeRowSet employeeRowSet = (EmployeeRowSet) JAXBContext
                    .newInstance(EmployeeRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            if (employeeRowSet != null && employeeRowSet.getEmployees() != null) {
                for (Employee employee : employeeRowSet.getEmployees()){
                    employee.setSessionId(sessionId);
                    employee.setPersonProfileId(personProfileId);

                    save(employee);
                }

                return employeeRowSet.getEmployees().size();
            }
        } catch (JAXBException e) {
            log.error("Ошибка импорта контрагентов", e);
        }

        return 0;
    }
}
