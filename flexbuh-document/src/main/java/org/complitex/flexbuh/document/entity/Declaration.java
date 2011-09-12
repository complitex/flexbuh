package org.complitex.flexbuh.document.entity;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 16:05
 */
@XmlRootElement(name = "DECLAR")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlSeeAlso(DeclarationValue.class)
public class Declaration implements Serializable{
    @XmlTransient
    private final Pattern NAME_PATTERN = Pattern.compile("(\\w\\d{2})(\\d{3})(\\d{2})");

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

    public void setName(String name){
        Matcher m = NAME_PATTERN.matcher(name);

        if (m.matches()){
            head.setCDoc(m.group(1));
            head.setCDocSub(m.group(2));
            head.setCDocVer(m.group(3));
        }else {
            throw new IllegalArgumentException("Имя должно содержать ровно 8 символов");
        }
    }

    public String getName(){
        return head.getCDoc() + head.getCDocSub() + head.getCDocVer();
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
            if (value.getName().equals(name) && (rowNum == null || value.getRowNum().equals(rowNum))){
                return value;
            }
        }

        return null;
    }

    public List<DeclarationValue> getValues(String name){
        List<DeclarationValue> list = new ArrayList<>();

        for (DeclarationValue value : values){
            if (value.getName().equals(name)){
                list.add(value);
            }
        }

        Collections.sort(list);

        return list;
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
