package org.complitex.flexbuh.document.entity;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 16:05
 */
@XmlRootElement(name = "DECLAR")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlSeeAlso(DeclarationValue.class)
public class Declaration implements Serializable{
    @XmlTransient
    private Long id;

    @XmlTransient
    private Long sessionId;

    @XmlTransient
    private List<DeclarationValue> values = new ArrayList<>();

    @XmlElement(name = "DECLARHEAD")
    private DeclarationHead head = new DeclarationHead();

    @XmlElementWrapper(name = "DECLARBODY")
    @XmlAnyElement
    private List<JAXBElement<DeclarationValue>> xmlValues = new ArrayList<JAXBElement<DeclarationValue>>();

    public Declaration() {
    }

    public void prepareXmlValues(){
        xmlValues.clear();

        for (DeclarationValue v : values){
            xmlValues.add(new JAXBElement<>(new QName(v.getName()), DeclarationValue.class, v.getValue() != null ? v : null));
        }
    }

    public DeclarationValue getValue(String name){
        return getValue(null, name);
    }

    public DeclarationValue getValue(Integer rowNum, String name){
        for (DeclarationValue value : values){
            if (name.equals(value.getName()) && (rowNum == null || rowNum.equals(value.getRowNum()))){
                return value;
            }
        }

        return null;
    }

    public void addValue(DeclarationValue value){
        values.add(value);
    }

    public void removeValue(String name){
        removeValue(null, name);
    }

    public void removeValue(Integer rowNum, String name){
        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            DeclarationValue value = values.get(i);

            if (name.equals(value.getName()) && (rowNum == null || rowNum.equals(value.getRowNum()))) {
                values.remove(i);
                return;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<DeclarationValue> getValues() {
        return values;
    }

    public void setValues(List<DeclarationValue> values) {
        this.values = values;
    }

    public DeclarationHead getHead() {
        return head;
    }

    public void setHead(DeclarationHead head) {
        this.head = head;
    }
}
