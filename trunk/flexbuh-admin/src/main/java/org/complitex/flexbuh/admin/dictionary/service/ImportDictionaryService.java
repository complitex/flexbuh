package org.complitex.flexbuh.admin.dictionary.service;

import org.complitex.flexbuh.admin.dictionary.entity.DictionaryConfig;
import org.complitex.flexbuh.admin.dictionary.web.DictionaryImportChildListener;
import org.complitex.flexbuh.admin.dictionary.web.DictionaryImportListener;
import org.complitex.flexbuh.common.entity.ILongId;
import org.complitex.flexbuh.common.entity.RowSet;
import org.complitex.flexbuh.common.entity.dictionary.*;
import org.complitex.flexbuh.common.exception.CriticalImportException;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.service.dictionary.*;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.04.12 12:00
 */
@Stateless
public class ImportDictionaryService{
    private final static Logger log = LoggerFactory.getLogger(ImportDictionaryService.class);

    private static final String SUB_DIR = "spr";

    @EJB
    private CurrencyBean currencyBean;

    @EJB
    private DocumentVersionBean documentVersionBean;

    @EJB
    private DocumentBean documentBean;

    @EJB
    private DocumentTermBean documentTermBean;

    @EJB
    private RegionBean regionBean;

    @EJB
    private FieldCodeBean fieldCodeBean;

    @EJB
    private TaxInspectionRegionBean taxInspectionRegionBean;

    @EJB
    private ConfigBean configBean;

    @Asynchronous
    public void process(DictionaryImportListener listener, List<DictionaryType> dictionaryTypes){
        try {
            for (DictionaryType dictionaryType : dictionaryTypes){
                processFile(listener.getChildListener(dictionaryType, true), dictionaryType);
            }
        } catch (Exception e) {
            listener.criticalError(e.getMessage());

            log.error("Критическая ошибка импорта" , e);
        }
    }

    private void processFile(DictionaryImportChildListener listener, DictionaryType dictionaryType)
            throws CriticalImportException {
        switch (dictionaryType){
            case CURRENCY:
                process(Currency.RS.class, currencyBean, listener, dictionaryType);
                break;
            case DOCUMENT:
                process(Document.RS.class, documentBean, listener, dictionaryType);
                break;
            case DOCUMENT_TERM:
                process(DocumentTerm.RS.class, documentTermBean, listener, dictionaryType);
                break;
            case DOCUMENT_VERSION:
                process(DocumentVersion.RS.class, documentVersionBean, listener, dictionaryType);
                break;
            case REGION:
                process(Region.RS.class, regionBean, listener, dictionaryType);
                break;
            case TAX_INSPECTION:
                process(TaxInspectionRegion.RS.class, taxInspectionRegionBean, listener, dictionaryType);
                break;
            case FIELD_CODE:
                fieldCodeBean.deleteAllFieldCode();
                process(FieldCode.RS.class, fieldCodeBean, listener, dictionaryType);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T extends AbstractDictionary, RS extends RowSet<T>> void process(
            Class<RS> rowSetClass, ICrudBean<T> crudBean, DictionaryImportChildListener listener, DictionaryType type)
            throws CriticalImportException {
        //Обработка
        listener.processing();

        //Директория импорта
        String dir = configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true);

        //Поток файла справочника
        InputStream inputStream;

        try {
            inputStream = FileUtil.getFileInputStream(dir, SUB_DIR, type.getFileName());
        } catch (Exception e) {
            throw new CriticalImportException(e, "Ошибка чтения файла {0}", type.getFileName());
        }

        //Создание объектов по xml
        RS rowSet;

        try {
            rowSet = (RS) JAXBContext.newInstance(rowSetClass).createUnmarshaller().unmarshal(inputStream);
        } catch (JAXBException e) {
            log.error("Ошибка создания объектов: " + e.getMessage(), e);
            listener.error(e.getMessage());

            return;
        }

        //Всего
        listener.setTotal(rowSet.getRows().size());

        //Сохранение в базу данных
        ILongId newObject = null;

        try {
            for (T o : rowSet.getRows()){
                newObject = o;

                Long id = crudBean.getId(o);

                o.setId(id);
                o.setUploadDate(DateUtil.getCurrentDate());

                if(id == null){
                    crudBean.insert(o);
                    listener.inserted();
                }else {
                    crudBean.update(o);
                    listener.updated();
                }
            }
        } catch (Exception e) {
            log.error("Ошибка сохранения в базу данных: {}", new Event(e, EventCategory.IMPORT, newObject));
            listener.error(e.getMessage());

            throw new CriticalImportException(e, "Ошибка сохранения в базу данных");
        }

        //Обработано
        listener.processed();
        log.info("Всего: {}, добавлено: {}, обновлено: {}", new Object[]{listener.getTotal(), listener.getInserted() ,
                listener.getUpdated(), new Event(EventCategory.IMPORT, type.getModelClass())});
    }
}
