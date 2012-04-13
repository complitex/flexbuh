package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.exception.ImportException;
import org.complitex.flexbuh.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.04.12 17:03
 */
@Stateless
public class DocumentVersionService {
    private final static Logger log = LoggerFactory.getLogger(DocumentVersionService.class);

    @EJB
    private DocumentVersionBean documentVersionBean;

    public void processImport(InputStream inputStream) throws ImportException {
        try {
            DocumentVersion.RS rowSet = (DocumentVersion.RS) JAXBContext.newInstance(DocumentVersion.RS.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            for (DocumentVersion documentVersion : rowSet.getRows()){
                documentVersion.setUploadDate(DateUtil.getCurrentDate());

                //todo validation

                if (documentVersionBean.isExist(documentVersion)){
                    documentVersionBean.update(documentVersion);
                }else {
                    documentVersionBean.save(documentVersion);
                }
            }
        } catch (Exception e) {
            throw new ImportException(e, "Ошибка импорта версий документов");
        }
    }
}
