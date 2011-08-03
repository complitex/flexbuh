package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.TemplateFO;
import org.complitex.flexbuh.document.entity.TemplateXSD;
import org.complitex.flexbuh.document.entity.TemplateXSL;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:30
 */
@Stateless
public class ImportTemplateService {
    @EJB
    private TemplateBean templateBean;

    public void importAllTemplates(String path) throws IOException {
        File dir = new File(path);

        //XSL
        for (File f : new File(dir, "xsl").listFiles()){
            templateBean.save(new TemplateXSL(getName(f), getData(f)));
        }

        //XSD
        for (File f : new File(dir, "xsd").listFiles()){
            templateBean.save(new TemplateXSD(getName(f), getData(f)));
        }

        //FO
        for (File f : new File(dir, "fo").listFiles()){
            templateBean.save(new TemplateFO(getName(f), getData(f)));
        }
    }

    private String getData(File file) throws IOException {
        String line;
        String data = "";

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        while ((line = bufferedReader.readLine()) != null){
            data += line + "\n";
        }

        return data;
    }

    private String getName(File f){
        return f.getName().substring(0, f.getName().lastIndexOf('.'));
    }
}
