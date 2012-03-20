package org.complitex.flexbuh.common.util;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.03.12 13:43
 */
public class ZipUtil {
    public final static int BUFFER = 8192;

    public static void writeZip(InputStream inputStream, ZipOutputStream zipOutputStream, String dir, String fileName) throws IOException {
        byte data[] = new byte[BUFFER];

        try (BufferedInputStream buf = new BufferedInputStream(inputStream, BUFFER)) {
            ZipEntry zipEntry = new ZipEntry(dir != null ? dir + "/" + fileName : fileName);

            zipOutputStream.putNextEntry(zipEntry);

            int count;
            while ((count = buf.read(data, 0, BUFFER)) != -1) {
                zipOutputStream.write(data, 0, count);
            }
        }
    }

    public static <T> void writeZip(Class<T> _class, T jaxbElement, ZipOutputStream zipOutputStream, String dir,
                                    String fileName) throws JAXBException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XmlUtil.writeXml(_class, jaxbElement, outputStream);

        writeZip(new ByteArrayInputStream(outputStream.toByteArray()), zipOutputStream, dir, fileName);
    }
}
