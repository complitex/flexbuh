package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.admin.importexport.entity.DictionaryConfig;
import org.complitex.flexbuh.common.entity.RowSet;
import org.complitex.flexbuh.common.entity.dictionary.*;
import org.complitex.flexbuh.common.exception.ImportException;
import org.complitex.flexbuh.common.service.AbstractImportListener;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.dictionary.*;
import org.complitex.flexbuh.common.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
    private ImportDictionaryJAXBService importDictionaryJAXBService;

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
    public void process(ImportListener<String> listener, List<DictionaryType> dictionaryTypes){
        listener.begin();

        try {
            for (DictionaryType dictionaryType : dictionaryTypes){
                processFile(listener, dictionaryType);
            }

            listener.completed();
        } catch (ImportException e) {
            listener.error(e.getMessage());

            log.error("Критическая Ошибка импорта", e);
        }
    }

    private void processFile(ImportListener<String> listener, DictionaryType dictionaryType) throws ImportException {
        try {
            switch (dictionaryType){
                case CURRENCY:
                    process(Currency.RS.class, currencyBean, dictionaryType);
                    break;
                case DOCUMENT:
                    process(Document.RS.class, documentBean, dictionaryType);
                    break;
                case DOCUMENT_TERM:
                    process(DocumentTerm.RS.class, documentTermBean, dictionaryType);
                    break;
                case DOCUMENT_VERSION:
                    process(DocumentVersion.RS.class, documentVersionBean, dictionaryType);
                    break;
                case REGION:
                    process(Region.RS.class, regionBean, dictionaryType);
                    break;
                case TAX_INSPECTION:
                    process(TaxInspectionRegion.RS.class, taxInspectionRegionBean, dictionaryType);
                    break;
                case FIELD_CODE:
                    fieldCodeBean.deleteAllFieldCode();
                    process(FieldCode.RS.class, fieldCodeBean, dictionaryType);
                    break;
            }

            listener.processed(dictionaryType.getFileName());
        } catch (RuntimeException e){
            throw new ImportException(e, "Критическая ошибка импорта");
        } catch (Exception e) {
            listener.skip(dictionaryType.getFileName());

            log.error("Ошибка импорта файла", e);
        }
    }

    private <T extends AbstractDictionary, RS extends RowSet<T>> void process(Class<RS> rowSetClass,
                                                                              ICrudBean<T> crudBean,
                                                                              DictionaryType dictionaryType)
            throws ImportException {
        String dir = configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true);

        importDictionaryJAXBService.process(rowSetClass, crudBean, new AbstractImportListener<T>(){},
                FileUtil.getFileInputStream(dir, SUB_DIR, dictionaryType.getFileName()));
    }
}
