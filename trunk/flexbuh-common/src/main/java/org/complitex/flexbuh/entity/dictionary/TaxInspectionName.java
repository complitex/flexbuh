package org.complitex.flexbuh.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.complitex.flexbuh.entity.LocalizedString;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 13:57
 */
public class TaxInspectionName extends LocalizedString {
    private Long taxInspectionId;

    public Long getTaxInspectionId() {
        return taxInspectionId;
    }

    public void setTaxInspectionId(Long taxInspectionId) {
        this.taxInspectionId = taxInspectionId;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
				append("taxInspectionId", getTaxInspectionId()).
				append(super.toString()).toString();
	}
}
