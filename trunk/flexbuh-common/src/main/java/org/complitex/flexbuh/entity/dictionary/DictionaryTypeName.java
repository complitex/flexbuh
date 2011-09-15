package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 09.08.11 11:01
 */
public class DictionaryTypeName extends LocalizedString {
    private Long dictionaryTypeId;

    public Long getDictionaryTypeId() {
        return dictionaryTypeId;
    }

    public void setDictionaryTypeId(Long dictionaryTypeId) {
        this.dictionaryTypeId = dictionaryTypeId;
    }
}
