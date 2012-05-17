package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.web.component.NoCacheLink;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.exception.DeclarationParseException;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.nio.charset.Charset;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.10.11 16:18
 */
public class DeclarationXmlLink extends NoCacheLink {
    private final static Logger log = LoggerFactory.getLogger(DeclarationXmlLink.class);

    @EJB
    private DeclarationService declarationService;

    private Declaration declaration;
    
    public DeclarationXmlLink(String id, Declaration declaration) {
        super(id);
        
        this.declaration = declaration;
    }

    @Override
    public void onClick() {
        try {
            //clear id before marshal
            Long oldId = declaration.getId();
            Long oldPersonProfileId = declaration.getPersonProfileId();
            declaration.setId(null);
            declaration.setPersonProfileId(null);

            String data = declarationService.getString(declaration, "windows-1251", false);

            //recover id
            declaration.setId(oldId);
            declaration.setPersonProfileId(oldPersonProfileId);

            StringResourceStream stringResourceStream = new StringResourceStream(data, "application/xml");
            stringResourceStream.setCharset(Charset.forName("windows-1251"));
            stringResourceStream.setLastModified(Time.now());

            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    stringResourceStream, declaration.getFileName() + ".xml"));

            log.info("Документ выгружен", new Event(EventCategory.EXPORT, declaration));
        } catch (DeclarationParseException e) {
            log.error("Ошибка выгрузки документа", e);
        }
    }
}
