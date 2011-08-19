package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.TemplateXSD;
import org.complitex.flexbuh.document.entity.TemplateXSL;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:30
 */
@SuppressWarnings({"EjbProhibitedPackageUsageInspection"})
@Stateless
public class ImportTemplateService {
    public static final String FILE_ENCODING = "CP1251";

    @EJB
    private TemplateBean templateBean;

    public void importAllTemplates(String path) throws IOException {
        File dir = new File(path);

        //XSL
        for (File f : new File(dir, "xsl").listFiles()){
            templateBean.save(new TemplateXSL(getName(f), changeWhitespaceUTF8(getData(f))));
        }

        //XSD
        for (File f : new File(dir, "xsd").listFiles()){
            templateBean.save(new TemplateXSD(getName(f), getData(f)));
        }

        //FO
//        for (File f : new File(dir, "fo").listFiles()){
//            templateBean.save(new TemplateFO(getName(f), getData(f)));
//        }
    }

    private String getData(File file) throws IOException {
        StringBuilder data = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), FILE_ENCODING));

        String line;

        while ((line = br.readLine()) != null){
            data.append(line).append('\n');
        }

        br.close();

        return data.toString();
    }

    private String getName(File f){
        return f.getName().substring(0, f.getName().lastIndexOf('.'));
    }

    private String changeWhitespaceUTF8(String s){
        return s.replaceAll("&amp;nbsp;", "&amp;#160;");
    }
}
