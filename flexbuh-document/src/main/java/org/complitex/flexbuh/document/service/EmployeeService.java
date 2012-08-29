package org.complitex.flexbuh.document.service;

import com.google.common.io.ByteStreams;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.05.12 16:32
 */
@Stateless
public class EmployeeService {
    private final static Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final static String FZL_CHARSET = "cp866";

    @EJB
    private EmployeeBean employeeBean;

    public void save(Long sessionId, Long personProfileId, String fileName, InputStream inputStream,
                     IProcessListener<Employee> listener){
        if (fileName.toLowerCase().contains(".fzl")){
            saveFzl(sessionId, personProfileId, inputStream, listener);
        }else {
            saveXml(sessionId, personProfileId, inputStream, listener);
        }
    }

    private void saveXml(Long sessionId, Long personProfileId, InputStream inputStream,
                         IProcessListener<Employee> listener) {
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

    private void saveFzl(Long sessionId, Long personProfileId, InputStream inputStream,
                         IProcessListener<Employee> listener){
        Employee employee = null;

        try {
            //buffer
            byte[] array = ByteStreams.toByteArray(inputStream);
            ByteBuffer buffer = ByteBuffer.wrap(array);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            int rows = buffer.getInt();

            int ptr = 4;

            for (int i = 0; i < rows; i++){
                //inn
                byte[] innBuf = new byte[11];
                buffer.get(innBuf);

                //name
                byte[] nameBuf = new byte[61];
                buffer.get(nameBuf);

                //dates
                int dateInDiff = unsignedToBytes(buffer.getShort(ptr + 225));
                int dateOutDiff = unsignedToBytes(buffer.getShort(ptr + 227));
                int birthdayDiff = unsignedToBytes(buffer.getShort(ptr + 335));

                //next row
                ptr += 342;
                buffer.position(ptr);

                //create employee
                employee = new Employee(sessionId, personProfileId);

                String name = new String(nameBuf, Charset.forName(FZL_CHARSET)).trim();
                employee.setHname(name.replace('Ў', 'I').replace('ў', 'i'));

                employee.setHtin(Long.parseLong(new String(innBuf, Charset.forName(FZL_CHARSET)).trim()));
                employee.setHbirthday(fzlCalcDate(birthdayDiff - 8708));
                employee.setHdateIn(fzlCalcDate(dateInDiff - 1));
                employee.setHdateOut(fzlCalcDate(dateOutDiff - 1));

                //save
                employeeBean.save(employee);

                log.info("Сотрудник загружен", new Event(EventCategory.IMPORT, employee));

                if (listener != null){
                    listener.onSuccess(employee);
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

    private int unsignedToBytes(short shortVal) {
        return shortVal >= 0 ? shortVal : 0x10000 + shortVal;
    }

    private Date fzlCalcDate(int diff){
        if (diff < 2){
            return null;
        }

        // Базовая дата - 31/12/1899
        Calendar base = Calendar.getInstance();
        base.set(1899, Calendar.DECEMBER, 31);

        base.add(Calendar.DATE, diff);

        return base.getTime();
    }

    public InputStream getInputStream(Long sessionId){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            EmployeeRowSet employeeRowSet = new EmployeeRowSet(employeeBean.getAllEmployees(sessionId), true);
            EmployeeRowSet logEmployeeRowSet = new EmployeeRowSet(employeeBean.getAllEmployees(sessionId));

            //jaxb preprocess, set not null values
            for (Employee employee : employeeRowSet.getEmployees()){
                if (employee.getHtin() == null){
                    employee.setHtin(0L);
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
