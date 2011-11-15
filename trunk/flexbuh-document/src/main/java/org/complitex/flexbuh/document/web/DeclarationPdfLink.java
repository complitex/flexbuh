package org.complitex.flexbuh.document.web;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.10.11 16:05
 */
public class DeclarationPdfLink extends Link {
    private final static Logger log = LoggerFactory.getLogger(DeclarationPdfLink.class);

    @EJB
    private DeclarationService declarationService;

    private Declaration declaration;

    public DeclarationPdfLink(String id, Declaration declaration) {
        super(id);

        this.declaration = declaration;
    }

    @Override
    public void onClick() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        declarationService.writePdf(declaration, outputStream);

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
                }, declaration.getFileName() + ".pdf"));

    }
}
