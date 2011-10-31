package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.util.DateUtil;
import org.w3c.dom.Element;

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
    private List xmlValues = new ArrayList();

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

    @XmlTransient
    private Document document;

    @XmlTransient
    private PersonProfile personProfile;

    public Declaration() {
        head.setDFill(DeclarationUtil.getString(DateUtil.getCurrentDate()));
    }

    public Declaration(String templateName) {
        this();
        setTemplateName(templateName);
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
        return head.getCDoc() + head.getCDocSub() + String.format("%02d", head.getCDocVer());
    }

    @SuppressWarnings("MalformedFormatString")
    public String getFileName(){
        return String.format("%02d%02d%010d%s%s%02d%s%02d%07d%d%02d%d%04d", //todo check format
                head.getCReg(), head.getCRaj(), head.getTin(), head.getCDoc(), head.getCDocSub(), head.getCDocVer(),
                head.getCDocStan(), head.getCDocType(), head.getCDocCnt(), head.getPeriodType(), head.getPeriodMonth(),
                head.getPeriodYear(), head.getCStiOrig());
    }

    public void setFileName(String fileName){
        //nothing
    }

    @SuppressWarnings("unchecked")
    public void prepareXmlValues(){
        xmlValues.clear();

        for (DeclarationValue v : declarationValues){
            DeclarationValue value = (v.getValue() != null && !v.getValue().isEmpty()) ? v : null;

            xmlValues.add(new JAXBElement(new QName(v.getName()), DeclarationValue.class, DeclarationValue.class, value));
        }
    }

    public List getXmlValues() {
        return xmlValues;
    }

    public void fillValuesFromXml(){
        declarationValues.clear();

        for (Object xmlValue :xmlValues){
            Element element = (Element) xmlValue;

            String rowNum = element.getAttribute("ROWNUM");
            Integer rowNumInt = (rowNum != null && !rowNum.isEmpty()) ? Integer.valueOf(rowNum) : null;
            boolean isNil = "true".equals(element.getAttribute("xsi:nil"));

            addDeclarationValue(new DeclarationValue(rowNumInt, element.getTagName(), !isNil ? element.getTextContent() : null));
        }
    }

    public DeclarationValue getDeclarationValue(String name){
        return getDeclarationValue(null, name);
    }

    public DeclarationValue getDeclarationValue(Integer rowNum, String name){
        for (DeclarationValue declarationValue : declarationValues){
            if (name.equals(declarationValue.getName()) && (rowNum == null || rowNum.equals(declarationValue.getRowNum()))){
                return declarationValue;
            }
        }

        return null;
    }

    public DeclarationValue getDeclarationValueByType(String type){
        for (DeclarationValue declarationValue : declarationValues){
            if (declarationValue.getType() != null && declarationValue.getType().equals(type)){
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
    
    public int getDeclarationValuesCount(String name){
        int count = 0;

        for (DeclarationValue declarationValue : declarationValues){
            if (declarationValue.getName().equals(name)){
                count++;
            }
        }

        return count;
    }

    public int getDeclarationValuesCountByMask(String mask){
        mask = mask.replace("XXXX", "");

        int count = 0;

        for (DeclarationValue declarationValue : declarationValues){
            if (declarationValue.getName().indexOf(mask) == 0){
                count++;
            }
        }

        return count;
    }

    public void addDeclarationValue(DeclarationValue value){
        declarationValues.add(value);
    }

    public void fillValue(String name, String value){
        DeclarationValue declarationValue = getDeclarationValue(name);

        if (declarationValue != null){
            declarationValue.setValue(value);
        }
    }

    public void fillValueByType(String type, String value){
        DeclarationValue declarationValue = getDeclarationValueByType(type);

        if (declarationValue != null){
            declarationValue.setValue(value);
        }
    }

    public void removeDeclarationValue(DeclarationValue declarationValue){
        declarationValues.remove(declarationValue);
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

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;

        head.setCDoc(document.getCDoc());
        head.setCDocSub(document.getCDocSub());
        name = document.getNameUk(); //todo add locale
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

    public PersonProfile getPersonProfile() {
        return personProfile;
    }

    public void setPersonProfile(PersonProfile personProfile) {
        this.personProfile = personProfile;
    }
}
