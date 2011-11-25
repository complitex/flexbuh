package org.complitex.flexbuh.common.entity.dictionary;

/**
 * @author Pavel Sknar
 *         Date: 25.11.11 10:51
 */
public class TaxInspectionFilter extends PeriodDictionaryFilter {

	private Integer cSti;
	private Integer cReg;
	private Integer cRaj;
	private Integer tSti;

    private String nameUk;
    private String nameRajUk;

	public Integer getcSti() {
		return cSti;
	}

	public void setcSti(Integer cSti) {
		this.cSti = cSti;
	}

	public Integer getcReg() {
		return cReg;
	}

	public void setcReg(Integer cReg) {
		this.cReg = cReg;
	}

	public Integer getcRaj() {
		return cRaj;
	}

	public void setcRaj(Integer cRaj) {
		this.cRaj = cRaj;
	}

	public Integer gettSti() {
		return tSti;
	}

	public void settSti(Integer tSti) {
		this.tSti = tSti;
	}

	public String getNameUk() {
		return nameUk;
	}

	public void setNameUk(String nameUk) {
		this.nameUk = nameUk;
	}

	public String getNameRajUk() {
		return nameRajUk;
	}

	public void setNameRajUk(String nameRajUk) {
		this.nameRajUk = nameRajUk;
	}
}
