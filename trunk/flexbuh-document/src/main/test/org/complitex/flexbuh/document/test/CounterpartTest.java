package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 14:37
 */
public class CounterpartTest {
    public static void main(String... args) throws JAXBException, FileNotFoundException {
        CounterpartRowSet counterpartRowSet = (CounterpartRowSet) JAXBContext
                .newInstance(CounterpartRowSet.class)
                .createUnmarshaller()
                .unmarshal(new FileInputStream("C:\\OPZ\\spr\\spr_contragents.xml"));

        System.out.println(counterpartRowSet);

        EmployeeRowSet employeeRowSet = (EmployeeRowSet) JAXBContext
                        .newInstance(EmployeeRowSet.class)
                        .createUnmarshaller()
                        .unmarshal(new FileInputStream("C:\\OPZ\\spr\\spr_works.xml"));

        for (Employee employee : employeeRowSet.getEmployees()){
            employee.updateDates();
        }

        System.out.println(employeeRowSet);
    }
}
