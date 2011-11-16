package org.complitex.flexbuh.document.service;

import org.apache.fop.apps.Driver;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.exception.DeclarationZipException;
import org.complitex.flexbuh.document.fop.FopConfiguration;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.common.entity.template.TemplateFO;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.11.11 16:04
 */
@Stateless
public class DeclarationService {
    private final static Logger log = LoggerFactory.getLogger(DeclarationService.class);

    static final int BUFFER = 2048;

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private TemplateBean templateBean;

    public void writeXmlZip(List<Long> declarationIds, OutputStream outputStream) throws DeclarationZipException {
        try {
            List<Declaration> declarations = declarationBean.getDeclarations(declarationIds);

            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

            byte data[] = new byte[BUFFER];

            for (Declaration declaration : declarations){
                String xml = DeclarationUtil.getString(declaration);

                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(xml.getBytes("UTF-8")), BUFFER);

                ZipEntry zipEntry = new ZipEntry(declaration.getFileName() + ".xml");

                zipOutputStream.putNextEntry(zipEntry);
                int count;
                while((count = inputStream.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }
                inputStream.close();
            }

            zipOutputStream.close();
        } catch (Exception e) {
            throw new DeclarationZipException("Ошибка создания архива XML документов", e);
        }
    }

    public void writePdfZip(List<Long> declarationIds, OutputStream outputStream) throws DeclarationZipException {
        try {
            List<Declaration> declarations = declarationBean.getDeclarations(declarationIds);

            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

            byte data[] = new byte[BUFFER];

            for (Declaration declaration : declarations){
                ByteArrayOutputStream pdf = new ByteArrayOutputStream();
                writePdf(declaration, pdf);

                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(pdf.toByteArray()), BUFFER);

                ZipEntry zipEntry = new ZipEntry(declaration.getFileName() + ".pdf");

                zipOutputStream.putNextEntry(zipEntry);
                int count;
                while((count = inputStream.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }
                inputStream.close();
            }

            zipOutputStream.close();
        } catch (Exception e) {
            throw new DeclarationZipException("Ошибка создания архива PDF документов", e);
        }
    }

    public void writePdf(Declaration declaration, OutputStream outputStream){
        try {
            TemplateFO templateFO = templateBean.getTemplateFO(declaration.getTemplateName());

            FopConfiguration.init();

            Driver driver = new Driver();

            driver.setRenderer(Driver.RENDER_PDF);


            driver.setOutputStream(outputStream);

            Result res = new SAXResult(driver.getContentHandler());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(templateFO.getData())));

            Source src = new StreamSource(new StringReader(DeclarationUtil.getString(declaration)));

            transformer.transform(src, res);
        } catch (Exception e) {
            log.error("Ошибка создания PDF");
        }
    }

}
