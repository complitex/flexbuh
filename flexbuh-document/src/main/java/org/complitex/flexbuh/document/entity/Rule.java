package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.08.11 16:40
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Rule implements Serializable{
    @XmlAttribute(name = "rownum")
    private String rowNum;

    @XmlAttribute(name = "c_doc_rowc")
    private String cDocRowC;

    @XmlAttribute(name = "sign")
    private String sign;

    @XmlAttribute(name = "expression")
    private String expression;

    @XmlAttribute(name = "description")
    private String description;

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public String getCDocRowC() {
        return cDocRowC;
    }

    public void setCDocRowC(String cDocRowC) {
        this.cDocRowC = cDocRowC;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
