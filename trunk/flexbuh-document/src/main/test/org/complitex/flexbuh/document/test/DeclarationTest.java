package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.11 15:30
 */
public class DeclarationTest {
    public static void main(String... args) throws JAXBException, FileNotFoundException {
        testJavaToXml();
    }

    private static void testJavaToXml() throws JAXBException {
        Declaration declaration = new Declaration();

        declaration.getHead().setTin(11);
        declaration.getHead().setCDoc("J01");
        declaration.getHead().setCDocCnt(1);
        declaration.getHead().setCDocStan(2);
        declaration.getHead().setCDocSub("00");
        declaration.getHead().setCDocType(3);
        declaration.getHead().setCDocVer(10);
        declaration.getHead().setCRaj(4);
        declaration.getHead().setCReg(5);
        declaration.getHead().setCStiOrig(33);
        declaration.getHead().setDFill("d_fill_10");
        declaration.getHead().setPeriodMonth(6);
        declaration.getHead().setPeriodType(7);
        declaration.getHead().setPeriodYear(8);
        declaration.getHead().setSoftware("software_14");

        declaration.getDeclarationValues().add(new DeclarationValue(null, "name1", null));
        declaration.getDeclarationValues().add(new DeclarationValue(1, "name2", null));
        declaration.getDeclarationValues().add(new DeclarationValue(2, "name3", "value3"));

        //linked
        LinkedDeclaration ld = new LinkedDeclaration();
        ld.setDeclaration(declaration);
        ld.setFilename("2");
        ld.setNum("3");
        ld.setType("type");

        declaration.getLinkedDeclarations().add(ld);

        declaration.prepareXmlValues();

        JAXBContext context = JAXBContext.newInstance(Declaration.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, declaration.getTemplateName() + ".xsd");

        m.marshal(declaration, System.out);

        System.out.println(declaration.getFileName());
    }

}
