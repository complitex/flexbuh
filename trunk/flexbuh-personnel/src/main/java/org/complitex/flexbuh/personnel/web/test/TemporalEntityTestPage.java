package org.complitex.flexbuh.personnel.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.common.entity.TemporalEntityFilter;
import org.complitex.flexbuh.common.service.TemporalEntityBean;
import org.complitex.flexbuh.personnel.entity.Department;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 17:10
 */
public class TemporalEntityTestPage extends WebPage{
    @EJB
    private TemporalEntityBean temporalEntityBean;
    
    public TemporalEntityTestPage() {
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                load();
            }
        });
    }

    private void save(){
        Department department = new Department();

        department.setName("Hello Department!");
        department.setCurrent(true);
        department.setDateStart(new Date());

        temporalEntityBean.save(department);
    }

    private void load(){
        List<Department> list = temporalEntityBean.getTemporalEntities(new TemporalEntityFilter<>(Department.class));

        System.out.println(list);
    }

}
