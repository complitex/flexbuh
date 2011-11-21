package org.compliex.flexbux.common.test;

import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 14:37
 */
public class FieldCodeTest {
    public static void main(String... args) throws JAXBException, FileNotFoundException {
        marshall();
        unmarshall();
    }

    private static void marshall() throws JAXBException {
        FieldCode fieldCode = new FieldCode();
        fieldCode.setCodes(Arrays.asList("1", "2", "3", "4"));
        fieldCode.setFields(Arrays.asList(new Field("a", "b", "c", "d"), new Field("e", "f", "j", "h")));

        Marshaller marshaller = JAXBContext.newInstance(FieldCode.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(fieldCode, System.out);
    }

    private static void unmarshall() throws FileNotFoundException, JAXBException {
        FileInputStream inputStream = new FileInputStream("C:\\OPZ\\spr\\sprForFields.xml");

        Unmarshaller unmarshaller = JAXBContext.newInstance(FieldCodeRoot.class).createUnmarshaller();
        FieldCodeRoot fieldCodeRoot = (FieldCodeRoot) unmarshaller.unmarshal(inputStream);

        System.out.println(fieldCodeRoot.toString());
    }
}
