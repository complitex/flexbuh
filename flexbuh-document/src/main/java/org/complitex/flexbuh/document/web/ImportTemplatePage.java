package org.complitex.flexbuh.document.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.document.service.ImportTemplateService2;

import javax.ejb.EJB;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:32
 */
public class ImportTemplatePage extends WebPage{
    @EJB
    private ImportTemplateService2 importTemplateService2;

    public ImportTemplatePage() {
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                try {
                    importTemplateService2.importAllTemplates("C:\\OPZ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
