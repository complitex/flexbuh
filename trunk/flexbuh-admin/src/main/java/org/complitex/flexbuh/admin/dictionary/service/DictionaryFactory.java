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
    private final static  Map<String, Class<? extends AbstractDictionary>> entity = new HashMap<>();
    private final static Map<String, Class<? extends ICrudBean<?>>> bean = new HashMap<>();

    static {
        //Entity
        entity.put("currency", Currency.class);
        entity.put("document", Document.class);
        entity.put("document_term", DocumentTerm.class);
        entity.put("document_version", DocumentVersion.class);
        entity.put("field_code", FieldCode.class);
        entity.put("region", Region.class);
        entity.put("tax_inspection", TaxInspection.class);

        //Bean
        bean.put("currency", CurrencyBean.class);
        bean.put("document", DocumentBean.class);
        bean.put("document_term", DocumentTermBean.class);
        bean.put("document_version", DocumentVersionBean.class);
        bean.put("field_code", FieldCodeBean.class);
        bean.put("region", RegionBean.class);
        bean.put("tax_inspection", TaxInspectionBean.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDictionary> Class<T> getEntity(String entityName){
        return (Class<T>) entity.get(entityName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractDictionary> ICrudBean<T> getCrudBean(String entityName){
        return (ICrudBean<T>) EjbUtil.getBean(bean.get(entityName));
    }
}
