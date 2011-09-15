package org.complitex.flexbuh.entity.dictionary;

import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:56
 */
public class CurrencyName extends LocalizedString {
    private Long currencyId;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }
}
