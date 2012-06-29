package org.complitex.flexbuh.document.entity;

import org.complitex.flexbuh.common.entity.ILongId;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.util.StringUtil;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
@XmlAccessorType(value = XmlAccessType.NONE)
public class Declaration implements ILongId, Serializable{
    @XmlElement(name = "DECLARHEAD")
    private DeclarationHead head = new DeclarationHead();

    @XmlElementWrapper(name = "DECLARBODY")
    @XmlAnyElement
    private List xmlValues = new ArrayList();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");
    private static final Pattern TEMPLATE_NAME_PATTERN = Pattern.compile("(\\w\\d{2})(\\d{3})(\\d{2})");

    private Long id;
    private Long sessionId;

    @XmlElement(name = "FB_PERSON_PROFILE_ID") //todo use only for account zip
    private Long personProfileId;

    private PersonProfile personProfile;
    private Long parentId;
    private Declaration parent;
    private Long possibleParentId;
    private Date date;
    private String name;
    private Document document;
    private boolean validated;
    private String validateMessage;
    private boolean checked;
    private String checkMessage;

    private List<DeclarationValue> declarationValues = new ArrayList<>();

    public Declaration() {
        head.setDFill(DATE_FORMAT.format(DateUtil.getCurrentDate()));
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
        return String.format("%02d%02d%010d%s%s%02d%s%02d%07d%d%02d%d%04d",
                head.getCReg(), head.getCRaj(), head.getTin(), head.getCDoc(), head.getCDocSub(), head.getCDocVer(),
                head.getCDocStan(), head.getCDocType(), head.getCDocCnt(), head.getPeriodType(), head.getPeriodMonth(),
                head.getPeriodYear(), head.getCStiOrig());
    }

    public void setFileName(String fileName){
        //nothing
    }

    @SuppressWarnings("unchecked")
    public void prepareXmlValues(){
        if (head.getLinkedDeclarations() != null && head.getLinkedDeclarations().isEmpty()){
            head.setLinkedDeclarations(null);
        }

        xmlValues.clear();

        for (DeclarationValue v : declarationValues){
            DeclarationValue value = (v.getValue() != null && !v.getValue().isEmpty()) ? v : null;

            xmlValues.add(new JAXBElement(new QName(v.getName()), DeclarationValue.class, DeclarationValue.class, value));
        }
    }

    public void clearXmlValues(){
        xmlValues.clear();
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

    public String getValue(String name){
        DeclarationValue declarationValue = getDeclarationValue(name);

        return declarationValue != null ? declarationValue.getValue() : "";
    }

    //todo use map to improve performance
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

    public void fillValue(String name, Integer value){
        fillValue(name, StringUtil.getString(value));
    }

    public void fillValue(String name, String value, boolean force){
        DeclarationValue declarationValue = getDeclarationValue(name);

        if (declarationValue != null){
            declarationValue.setValue(value);
        }else if (force){
            declarationValues.add(new DeclarationValue(name, value));
        }
    }

    public void fillValue(String name, String value){
        fillValue(name, value, false);
    }

    public void fillValueByType(String type, Integer value){
        fillValue(type, StringUtil.getString(value));
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
        for (DeclarationValue dv : declarationValues) {
            if (name.equals(dv.getName()) && (rowNum == null || rowNum.equals(dv.getRowNum()))) {
                declarationValues.remove(dv);
                return;
            }
        }
    }

    public LinkedDeclaration getLinkedDeclaration(String name){
        if (head.getLinkedDeclarations() != null) {
            for (LinkedDeclaration linkedDeclaration : head.getLinkedDeclarations()){
                if (name.equals(linkedDeclaration.getName())){
                    return linkedDeclaration;
                }
            }
        }

        return null;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;

        head.setCDoc(document != null ? document.getCDoc() : null);
        head.setCDocSub(document != null ? document.getCDocSub() : null);

        name = document != null ? document.getNameUk() : null;
    }

    public void updateVersion(){
        head.setCDocVer(document.getVersion(head.getPeriodYear(), head.getPeriodMonth()));
    }

    public boolean hasLinkedDeclarations(){
        return head.getLinkedDeclarations() != null && !head.getLinkedDeclarations().isEmpty();
    }
    
    public String getFullName(){
        return getTemplateName() + " " + getName();
    }

    public String getShortName(){
        return head.getCDoc() + " " + head.getCDocSub();

    }

    public void addLinkedDeclaration(Document document){
        if (head.getLinkedDeclarations() == null){
            head.setLinkedDeclarations(new ArrayList<LinkedDeclaration>());
        }

        Declaration declaration = new Declaration();

        declaration.setDocument(document);
        declaration.setParent(this);
        declaration.getHead().setPeriodType(getHead().getPeriodType());
        declaration.getHead().setPeriodMonth(getHead().getPeriodMonth());
        declaration.getHead().setPeriodYear(getHead().getPeriodYear());
        declaration.getHead().setCDocVer(document.getVersion(head.getPeriodYear(), head.getPeriodMonth()));

        head.getLinkedDeclarations().add(new LinkedDeclaration(declaration));
    }

    /*Auto-generated Getters and Setters*/

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

    public Long getPersonProfileId() {
        return personProfileId;
    }

    public void setPersonProfileId(Long personProfileId) {
        this.personProfileId = personProfileId;
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

    public Long getPossibleParentId() {
        return possibleParentId;
    }

    public void setPossibleParentId(Long possibleParentId) {
        this.possibleParentId = possibleParentId;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getValidateMessage() {
        return validateMessage;
    }

    public void setValidateMessage(String validateMessage) {
        this.validateMessage = validateMessage;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCheckMessage() {
        return checkMessage;
    }

    public void setCheckMessage(String checkMessage) {
        this.checkMessage = checkMessage;
    }
}
