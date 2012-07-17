package org.complitex.flexbuh.admin.dictionary.service;

import org.complitex.flexbuh.common.entity.dictionary.*;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.service.dictionary.*;
import org.complitex.flexbuh.common.util.EjbUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.12 16:30
 */
public class DictionaryFactory {
    private final static  Map<String, Class<? extends AbstractDictionary>> ENTITY = new HashMap<>();
    private final static Map<String, Class<? extends ICrudBean<?>>> BEAN = new HashMap<>();

    static {
        //Entity
        ENTITY.put("currency", Currency.class);
        ENTITY.put("document", Document.class);
        ENTITY.put("document_term", DocumentTerm.class);
        ENTITY.put("document_version", DocumentVersion.class);
        ENTITY.put("field_code", FieldCode.class);
        ENTITY.put("region", Region.class);
        ENTITY.put("tax_inspection", TaxInspection.class);

        //Bean
        BEAN.put("currency", CurrencyBean.class);
        BEAN.put("document", DocumentBean.class);
        BEAN.put("document_term", DocumentTermBean.class);
        BEAN.put("document_version", DocumentVersionBean.class);
        BEAN.put("field_code", FieldCodeBean.class);
        BEAN.put("region", RegionBean.class);
        BEAN.put("tax_inspection", TaxInspectionBean.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDictionary> Class<T> getEntity(String entityName){
        return (Class<T>) ENTITY.get(entityName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDictionary> ICrudBean<T> getCrudBean(String entityName){
        return (ICrudBean<T>) EjbUtil.getBean(BEAN.get(entityName));
    }
}
