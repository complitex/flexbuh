import org.complitex.flexbuh.personnel.entity.Department;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 16:37
 */
public class TemporalEntityTest {
    public static void main(String... args){
        departmentTest();
    }

    private static void departmentTest(){
        Department department = new Department();

        department.setName("DepName");
        department.setCurrent(true);
        department.setDateStart(new Date());

        System.out.println(department.getFieldMap());
        System.out.println(department.getTableName());
    }

}
