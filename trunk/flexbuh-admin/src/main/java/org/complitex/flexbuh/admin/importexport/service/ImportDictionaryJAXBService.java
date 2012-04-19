package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.common.entity.RowSet;
import org.complitex.flexbuh.common.entity.dictionary.AbstractDictionary;
import org.complitex.flexbuh.common.exception.ImportException;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.04.12 14:59
 */
@Stateless
@LocalBean
public class ImportDictionaryJAXBService {
    private final static Logger log = LoggerFactory.getLogger(ImportDictionaryJAXBService.class);

    @SuppressWarnings("unchecked")
    public <T extends AbstractDictionary, RS extends RowSet<T>> void process(
            Class<RS> rowSetClass, ICrudBean<T> crudBean, ImportListener<T> listener, InputStream inputStream)
            throws ImportException {
        try {
            RS rowSet = (RS) JAXBContext.newInstance(rowSetClass)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            for (T o : rowSet.getRows()){
                o.setUploadDate(DateUtil.getCurrentDate());

                o.setId(crudBean.getId(o));

                crudBean.save(o);
            }

            listener.completed();
        } catch (Exception e) {
            throw new ImportException(e, "Ошибка импорта файла");
        }
    }
}
