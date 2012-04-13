package org.compliex.flexbux.common.test;

import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.04.12 17:13
 */
public class RowSetTest {
    public static void main(String... args) throws JAXBException, FileNotFoundException {
        DocumentVersion.RS rowSet = (DocumentVersion.RS) JAXBContext.newInstance(DocumentVersion.RS.class)
                            .createUnmarshaller()
                            .unmarshal(new FileInputStream("c:\\opz\\spr\\SPR_VER.XML"));

        System.out.println(rowSet);
    }
}
