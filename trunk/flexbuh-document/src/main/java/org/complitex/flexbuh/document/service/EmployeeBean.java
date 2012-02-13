package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeFilter;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

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

    @SuppressWarnings("unchecked")
    public List<Employee> getEmployees(EmployeeFilter filter){
        return sqlSession().selectList("selectEmployees", filter);
    }

    public Integer getEmployeesCount(EmployeeFilter filter){
        return (Integer) sqlSession().selectOne("selectEmployeesCount", filter);
    }

    public void save(Employee employee, Locale locale){
        fioBean.createFIO(employee.getFirstName(), employee.getMiddleName(), employee.getLastName(), locale);

        if (employee.getId() == null){
            sqlSession().insert("insertEmployee", employee);
        }else {
            sqlSession().update("updateEmployee", employee);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteEmployee", id);
    }

    public int save(Long sessionId, InputStream inputStream, Locale locale) {
        try {
            EmployeeRowSet employeeRowSet = (EmployeeRowSet) JAXBContext
                    .newInstance(EmployeeRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);
            
            Long personalProfileId = personProfileBean.getSelectedPersonProfileId(sessionId);

            if (employeeRowSet != null && employeeRowSet.getEmployees() != null) {
                for (Employee employee : employeeRowSet.getEmployees()){
                    employee.setSessionId(sessionId);
                    employee.setPersonProfileId(personalProfileId);

                    save(employee, locale);
                }

                return employeeRowSet.getEmployees().size();
            }
        } catch (JAXBException e) {
            log.error("Ошибка импорта контрагентов", e);
        }

        return 0;
    }
}
