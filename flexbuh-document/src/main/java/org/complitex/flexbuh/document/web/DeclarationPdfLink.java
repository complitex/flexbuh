package org.complitex.flexbuh.document.web;

import org.apache.fop.apps.Driver;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.fop.FopConfiguration;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.entity.template.TemplateFO;
import org.complitex.flexbuh.service.TemplateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.10.11 16:05
 */
public class DeclarationPdfLink extends Link {
    private final static Logger log = LoggerFactory.getLogger(DeclarationPdfLink.class);

    @EJB
    private TemplateBean templateBean;

    private Declaration declaration;
        
    public DeclarationPdfLink(String id, Declaration declaration) {
        super(id);

        this.declaration = declaration;
    }

    @Override
    public void onClick() {        
        try {
            TemplateFO templateFO = templateBean.getTemplateFO(declaration.getTemplateName());

            FopConfiguration.init();

            Driver driver = new Driver();

            driver.setRenderer(Driver.RENDER_PDF);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            driver.setOutputStream(outputStream);

            Result res = new SAXResult(driver.getContentHandler());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(templateFO.getData())));

            Source src = new StreamSource(new StringReader(DeclarationUtil.getString(declaration)));

            transformer.transform(src, res);

            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    new AbstractResourceStreamWriter(){

                        @Override
                        public void write(Response output) {
                            output.write(outputStream.toByteArray());
                        }

                        @Override
                        public Bytes length() {
                            return Bytes.bytes(outputStream.size());
                        }

                        @Override
                        public String getContentType() {
                            return "application/pdf";
                        }

                        @Override
                        public Time lastModifiedTime() {
                            return Time.now();
                        }
                    }, declaration.getTemplateName() + ".pdf"));
        } catch (Exception e) {
            log.error("Ошибка генерации pdf документа", e);

            throw new WicketRuntimeException(e);
        }
    }
}
