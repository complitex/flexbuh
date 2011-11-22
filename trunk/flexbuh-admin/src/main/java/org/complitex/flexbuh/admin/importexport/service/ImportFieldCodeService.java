package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeRoot;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.ImportXMLService;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.11.11 14:40
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportFieldCodeService extends ImportXMLService {
    private final static Logger log = LoggerFactory.getLogger(ImportFieldCodeService.class);

    @EJB
    private FieldCodeBean fieldCodeBean;

    @Resource
    private UserTransaction userTransaction;

    @Override
    public void process(Long sessionId, ImportListener listener, String fileName, InputStream inputStream, Date beginDate, Date endDate) {
        try {
            listener.begin();

            Unmarshaller unmarshaller = JAXBContext.newInstance(FieldCodeRoot.class).createUnmarshaller();

            FieldCodeRoot fieldCodeRoot = (FieldCodeRoot) unmarshaller.unmarshal(inputStream);

            userTransaction.begin();

            for (FieldCode fieldCode : fieldCodeRoot.getFieldCodes()) {
                fieldCodeBean.save(fieldCode);
            }

            userTransaction.commit();

            listener.completed();
        } catch (Exception e) {
            log.warn("Ошибка импорта справочника Поля ввода", e);
            listener.cancel();
        }
    }
}
