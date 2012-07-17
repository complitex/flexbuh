package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.markup.html.WebPage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.07.12 17:04
 */
public class DictionaryPages {
    private final static Map<String, Class<? extends WebPage>> LIST_PAGES = new HashMap<>();

    static {
        //Entity
        LIST_PAGES.put("currency", CurrencyList.class);
        LIST_PAGES.put("document", DocumentList.class);
        LIST_PAGES.put("document_term", DocumentTermList.class);
        LIST_PAGES.put("document_version", DocumentVersionList.class);
        LIST_PAGES.put("field_code", FieldCodeList.class);
        LIST_PAGES.put("region", RegionList.class);
        LIST_PAGES.put("tax_inspection", TaxInspectionList.class);
    }

    public static Class<? extends WebPage> getListPage(String entityName){
        return LIST_PAGES.get(entityName);
    }
}
