package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.RowSet;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 12:24
 */
@XmlRootElement(name = "row")
@XmlAccessorType(XmlAccessType.FIELD)
public class Region extends AbstractPeriodDictionary {
    @XmlRootElement(name = "rowset")
    @XmlSeeAlso(Region.class)
    public final static class RS extends RowSet<Region>{}

    @NotNull
    @XmlElement(name = "CODE")
	private Integer code;

    public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public boolean validate() {
		return super.validate() && code != null;
	}
}
