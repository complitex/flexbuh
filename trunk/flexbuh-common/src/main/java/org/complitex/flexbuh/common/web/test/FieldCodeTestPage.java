package org.complitex.flexbuh.common.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeFilter;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 17:38
 */
public class FieldCodeTestPage extends WebPage{
    @EJB
    private FieldCodeBean fieldCodeBean;

    public FieldCodeTestPage() {
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                testLoad();
            }
        });
    }

    private void testSave(){
        FieldCode fieldCode = new FieldCode();

        fieldCode.setCodes(Arrays.asList("1", "2", "3"));
        fieldCode.setFields(Arrays.asList(new Field("a", "b", "c", "d"), new Field("e", "f", "j", "h")));

        fieldCodeBean.save(fieldCode);
    }
    
    private void testLoad(){
        List<FieldCode> fieldCodes = fieldCodeBean.getFieldCodes(new FieldCodeFilter());

        System.out.println(fieldCodes);
    }
}
