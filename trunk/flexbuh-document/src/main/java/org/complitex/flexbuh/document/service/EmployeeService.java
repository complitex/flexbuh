package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.IProcessListener;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.05.12 16:32
 */
@Stateless
public class EmployeeService {
    private final static Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @EJB
    private EmployeeBean employeeBean;

    public void save(Long sessionId, Long personProfileId, InputStream inputStream, IProcessListener<Employee> listener) {
        Employee employee = null;

        try {
            EmployeeRowSet employeeRowSet = (EmployeeRowSet) JAXBContext
                    .newInstance(EmployeeRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            if (employeeRowSet != null && employeeRowSet.getEmployees() != null) {
                for (Employee e : employeeRowSet.getEmployees()){
                    employee = e;

                    e.setSessionId(sessionId);
                    e.setPersonProfileId(personProfileId);

                    employeeBean.save(employee);

                    log.info("Сотрудник загружен", new Event(EventCategory.IMPORT, e));

                    if (listener != null){
                        listener.onSuccess(e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки сотрудников", e);

            if (listener != null){
                listener.onError(employee, e);
            }else {
                throw new RuntimeException(e);
            }
        }
    }

    public InputStream getInputStream(Long sessionId){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            EmployeeRowSet employeeRowSet = new EmployeeRowSet(employeeBean.getAllEmployees(sessionId), true);
            EmployeeRowSet logEmployeeRowSet = new EmployeeRowSet(employeeBean.getAllEmployees(sessionId));

            //jaxb preprocess, set not null values
            for (Employee employee : employeeRowSet.getEmployees()){
                if (employee.getHtin() == null){
                    employee.setHtin(0);
                }
            }

            XmlUtil.writeXml(EmployeeRowSet.class, employeeRowSet, outputStream, "windows-1251");

            log.info("Выгрузка сотрудников", new Event(EventCategory.EXPORT, logEmployeeRowSet));

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Ошибка выгрузки сотрудников");
        }

        return null;

    }
}
