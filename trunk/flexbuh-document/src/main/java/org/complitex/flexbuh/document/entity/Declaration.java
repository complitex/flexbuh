package org.complitex.flexbuh.document.entity;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.07.11 16:05
 */
@XmlRootElement(name = "DECLAR")
@XmlSeeAlso(DeclarationValue.class)
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Declaration implements Serializable{
    @XmlTransient
    private final Pattern TEMPLATE_NAME_PATTERN = Pattern.compile("(\\w\\d{2})(\\d{3})(\\d{2})");

    @XmlTransient
    private Long id;

    @XmlTransient
    private Long sessionId;

    @XmlTransient
    private List<DeclarationValue> declarationValues = new ArrayList<>();

    @XmlElement(name = "DECLARHEAD")
    private DeclarationHead head = new DeclarationHead();

    @XmlElementWrapper(name = "DECLARBODY")
    @XmlAnyElement
    private List<JAXBElement<DeclarationValue>> xmlValues = new ArrayList<JAXBElement<DeclarationValue>>();

    @XmlElementWrapper(name = "LINKED_DOCS")
    @XmlElement(name = "DOC")
    private List<LinkedDeclaration> linkedDeclarations = new ArrayList<>();

    @XmlTransient
    private Long parentId;

    @XmlTransient
    private Declaration parent;

    @XmlTransient
    private Date date;

    @XmlTransient
    private String name;

    public Declaration() {
    }

    public Declaration(String name) {
        setTemplateName(name);
    }

    public void setTemplateName(String name){
        Matcher m = TEMPLATE_NAME_PATTERN.matcher(name);

        if (m.matches()){
            head.setCDoc(m.group(1));
            head.setCDocSub(m.group(2));
            head.setCDocVer(Integer.valueOf(m.group(3)));
        }else {
            throw new IllegalArgumentException("Имя должно содержать ровно 8 символов");
        }
    }

    public String getTemplateName(){
        return head.getCDoc() + head.getCDocSub() + (head.getCDocVer() < 10 ? "0" : "") + head.getCDocVer();
    }

    public void prepareXmlValues(){
        xmlValues.clear();

        for (DeclarationValue v : declarationValues){
            xmlValues.add(new JAXBElement<>(new QName(v.getName()), DeclarationValue.class, v.getValue() != null ? v : null));
        }
    }

    public List<JAXBElement<DeclarationValue>> getXmlValues() {
        return xmlValues;
    }

    public DeclarationValue getDeclarationValue(String name){
        return getDeclarationValue(null, name);
    }

    public DeclarationValue getDeclarationValue(Integer rowNum, String name){
        for (DeclarationValue declarationValue : declarationValues){
            if (declarationValue.getName().equals(name) && (rowNum == null || declarationValue.getRowNum().equals(rowNum))){
                return declarationValue;
            }
        }

        return null;
    }

    public List<DeclarationValue> getDeclarationValues(String name){
        List<DeclarationValue> list = new ArrayList<>();

        for (DeclarationValue declarationValue : declarationValues){
            if (declarationValue.getName().equals(name)){
                list.add(declarationValue);
            }
        }

        Collections.sort(list);

        return list;
    }

    public void addDeclarationValue(DeclarationValue value){
        declarationValues.add(value);
    }

    public void removeDeclarationValue(String name){
        removeDeclarationValue(null, name);
    }

    public void removeDeclarationValue(Integer rowNum, String name){
        for (int i = 0, valuesSize = declarationValues.size(); i < valuesSize; i++) {
            DeclarationValue value = declarationValues.get(i);

            if (name.equals(value.getName()) && (rowNum == null || rowNum.equals(value.getRowNum()))) {
                declarationValues.remove(i);
                return;
            }
        }
    }

    public LinkedDeclaration getLinkedDeclaration(String name){
        for (LinkedDeclaration linkedDeclaration : linkedDeclarations){
            if (name.equals(linkedDeclaration.getName())){
                return linkedDeclaration;
            }
        }

        return null;
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

    public List<DeclarationValue> getDeclarationValues() {
        return declarationValues;
    }

    public void setDeclarationValues(List<DeclarationValue> declarationValues) {
        this.declarationValues = declarationValues;
    }

    public DeclarationHead getHead() {
        return head;
    }

    public void setHead(DeclarationHead head) {
        this.head = head;
    }

    public List<LinkedDeclaration> getLinkedDeclarations() {
        return linkedDeclarations;
    }

    public void setLinkedDeclarations(List<LinkedDeclaration> linkedDeclarations) {
        this.linkedDeclarations = linkedDeclarations;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Declaration getParent() {
        return parent;
    }

    public void setParent(Declaration parent) {
        this.parent = parent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
