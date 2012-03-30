package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.03.12 14:37
 */
public class RowSetTest {
    public static void main(String... args) throws JAXBException {
        testCounterpart();
    }

    private static void testCounterpart() throws JAXBException {
        List<Counterpart> counterparts = new ArrayList<>();

        Counterpart c1 = new Counterpart();
        c1.setFirstName("F1");
        counterparts.add(c1);

        Counterpart c2 = new Counterpart();
        c2.setHname("H2");
        counterparts.add(c2);

        XmlUtil.writeXml(CounterpartRowSet.class, new CounterpartRowSet(counterparts), System.out, "windows-1251");
    }

    private static void testEmployee() throws JAXBException {
        List<Employee> employees = new ArrayList<>();

        Employee e1 = new Employee();
        e1.setFirstName("F1");
        employees.add(e1);

        Employee e2 = new Employee();
        e2.setHname("H2");
        employees.add(e2);

        XmlUtil.writeXml(EmployeeRowSet.class, new EmployeeRowSet(employees), System.out, "windows-1251");
    }
}
