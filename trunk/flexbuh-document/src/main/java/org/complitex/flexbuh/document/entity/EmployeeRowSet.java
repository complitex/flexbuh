package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.RowSetHeader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 14:42
 */
@XmlRootElement(name = "rowset")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class EmployeeRowSet {
    public final static String FILE_NAME = "spr_works.xml";

    @XmlElement(name = "header")
    private RowSetHeader header;

    @XmlElement(name = "row")
    private List<Employee> employees;

    public EmployeeRowSet() {
        header = new RowSetHeader("spr_works", "Довідник працючих");

        header.addColumn("HTIN", "Ідентифікаційний код", "right");
        header.addColumn("HNAME", "Прізвище, ім'я, по-батькові", "left");
        header.addColumn("HBIRTHDAY", "Дата народження", "left");
        header.addColumn("HDATE_IN", "Дата прийняття на роботу", "left");
        header.addColumn("HDATE_OUT", "Дата звільнення", "left");
    }

    public EmployeeRowSet(List<Employee> employees, boolean clearLocalId) {
        this();

        this.employees = employees;

        for (int i = 0, size = employees.size(); i < size; i++){
            Employee employee = employees.get(i);

            employee.setNum(i + 1);

            if (clearLocalId){
                employee.setId(null);
                employee.setPersonProfileId(null);
            }
        }
    }

    public EmployeeRowSet(List<Employee> employees) {
        this(employees, false);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
