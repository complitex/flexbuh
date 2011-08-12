package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.w3c.tidy.Tidy;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.11 15:30
 */
public class DeclarationTest {
    public static void main(String... args) throws JAXBException, FileNotFoundException {
        testHtmlToXml();
    }

    private static void testJavaToXml() throws JAXBException {
        Declaration declaration = new Declaration();

        declaration.setTin("tin_0");
        declaration.setCDoc("c_doc_1");
        declaration.setCDocCnt("c_doc_cnt_2");
        declaration.setCDocStan("c_doc_stan_3");
        declaration.setCDocSub("c_doc_sub_4");
        declaration.setCDocType("c_doc_type_5");
        declaration.setCDocVer("c_doc_ver_6");
        declaration.setCRaj("c_doc_raj_7");
        declaration.setCReg("c_doc_reg_8");
        declaration.setCStiOrig("c_stil_orig_9");
        declaration.setDFill("d_fill_10");
        declaration.setPeriodMonth("period_month_11");
        declaration.setPeriodType("period_type_12");
        declaration.setPeriodYear("period_year_13");
        declaration.setSoftware("software_14");

        declaration.getValues().add(new DeclarationValue("name1", null, null));
        declaration.getValues().add(new DeclarationValue("name2", "", "value2"));
        declaration.getValues().add(new DeclarationValue("name3", "", "value3"));

        declaration.prepareXmlValues();

        JAXBContext context = JAXBContext.newInstance(Declaration.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        m.marshal(declaration, System.out);
    }

    private static void testHtmlToXml() throws FileNotFoundException {
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setOutputEncoding("UTF-8");

        tidy.parse(new FileReader("c:\\TemplateTest.html"), System.out);
    }
}
