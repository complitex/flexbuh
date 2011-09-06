package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.08.11 16:40
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Rule implements Serializable{
    @XmlTransient
    private final Pattern pattern = Pattern.compile("\\^(\\w*\\.?\\w*)");

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

    @XmlTransient
    private String cDocRowCId;

    @XmlTransient
    private List<String> expressionIds;

    public String getCDocRowCId(){
        if (cDocRowCId == null && cDocRowC != null){
            cDocRowCId = cDocRowC.replace("^", "");
        }

        return cDocRowCId;
    }

    public List<String> getExpressionIds(){
        if (expressionIds == null && expression != null){
            expressionIds = extractIds(expression);
        }

        return expressionIds;
    }

    private List<String> extractIds(String expression){
        List<String> ids = new ArrayList<>();

        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()){
            ids.add(matcher.group(1));
        }

        return ids;
    }

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
