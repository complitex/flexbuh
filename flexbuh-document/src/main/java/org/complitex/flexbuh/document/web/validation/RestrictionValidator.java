package org.complitex.flexbuh.document.web.validation;

import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.08.11 17:13
 */
public class RestrictionValidator<T> implements INullAcceptingValidator<T>, IValidationError {
    private final static List<String> NUMBER_BASE = Arrays.asList("xs:nonNegativeInteger", "xs:integer", "xs:unsignedLong");

    private String name;
    private String documentation;
    private String base;
    private Pattern pattern;
    private List<String> enumeration = new ArrayList<>();
    private Integer length;
    private Integer minLength;
    private Integer maxLength;
    private Integer totalDigits;
    private Long minInclusive;
    private Long maxInclusive;

    private boolean complex = false;
    private boolean requiredRowNum = false;

    private Element typeElement;

    private String errorMessage;

    private Locale locale;

    private final static Map<String, ResourceBundle> resourceBundleMap = new ConcurrentHashMap<>();

    public RestrictionValidator(Element typeElement) {
        this(typeElement, Locale.getDefault());
    }

    public RestrictionValidator(Element typeElement, Locale locale) {
        this.typeElement = typeElement;
        this.locale = locale;

        parse();
    }

    private void parse(){
        complex = "xs:complexType".equals(typeElement.getTagName());

        name = typeElement.getAttribute("name");

        NodeList documentationNodeList = typeElement.getElementsByTagName("xs:documentation");
        if (documentationNodeList.getLength() > 0){
            documentation = documentationNodeList.item(0).getTextContent();
        }

        if (!complex){
            parseSimpleType();
        }else {
            parseComplexType();
        }
    }

    private void parseSimpleType(){
        NodeList restrictionNodeList =  typeElement.getElementsByTagName("xs:restriction");

        if (restrictionNodeList.getLength() > 0){
            Element restrictionElement = (Element) restrictionNodeList.item(0);

            base = restrictionElement.getAttribute("base");

            NodeList children = restrictionElement.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i){
                Node child = children.item(i);

                if (child instanceof Element){
                    Element element = (Element) child;
                    String value = element.getAttribute("value");

                    switch (element.getTagName()){
                        case "xs:length":
                            length = Integer.valueOf(value);
                            break;
                        case "xs:pattern":
                            pattern = Pattern.compile(value);
                            break;
                        case "xs:enumeration":
                            enumeration.add(value);
                            break;
                        case "xs:minInclusive":
                            minInclusive = Long.valueOf(value);
                            break;
                        case "xs:maxInclusive":
                            maxInclusive = Long.valueOf(value);
                            break;
                        case "xs:totalDigits":
                            totalDigits = Integer.valueOf(value);
                            break;
                        case "xs:maxLength":
                            maxLength = Integer.valueOf(value);
                            break;
                        case "xs:minLength":
                            minLength = Integer.valueOf(value);
                            break;
                    }
                }
            }
        }
    }

    private void parseComplexType(){
        Element extension = (Element) typeElement.getElementsByTagName("xs:extension").item(0);

        base = extension.getAttribute("base");

        NodeList attributeNodeList = extension.getElementsByTagName("xs:attribute");

        if (attributeNodeList.getLength() > 0){
            Element attribute = (Element) attributeNodeList.item(0);

            requiredRowNum = "ROWNUM".equals(attribute.getAttribute("name")) && "required".equals(attribute.getAttribute("use"));
        }
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        if (validatable.getValue() != null) {
            String valueString = validatable.getValue().toString();

            if (length != null && valueString.length() != length){
                error(validatable, "length");
            }

            if (minLength != null && valueString.length() < minLength){
                error(validatable, "minLength");
            }

            if (maxLength != null && valueString.length() > maxLength){
                error(validatable, "maxLength");
            }

            if (totalDigits != null && valueString.length() == totalDigits){
                error(validatable, "totalDigits");
            }

            if (pattern != null && !pattern.matcher(valueString).matches()){
                error(validatable, "pattern");
            }

            if (enumeration.size() > 0 && !enumeration.contains(valueString)){
                error(validatable, "enumeration");
            }

            if (NUMBER_BASE.contains(base)){
                try {
                    Long number = Long.valueOf(valueString);

                    if (minInclusive != null && number < minInclusive){
                        error(validatable, "minInclusive");
                    }

                    if (maxInclusive != null && number > maxInclusive){
                        error(validatable, "maxInclusive");
                    }

                } catch (NumberFormatException e) {
                    error(validatable, "notNumber");
                }
            }
        }
    }

    private void error(IValidatable<T> validatable, String key){
        Object argument = null;

        switch (key){
            case "minLength":
                argument = minLength;
                break;
            case "maxLength":
                argument = maxLength;
                break;
            case "totalDigits":
                argument = totalDigits;
                break;
            case "length":
                argument = length;
                break;
            case "minInclusive":
                argument = minInclusive;
                break;
            case "maxInclusive":
                argument = maxInclusive;
                break;
        }



        errorMessage = argument != null ? MessageFormat.format(getString(key), argument) : getString(key);

        errorMessage += ": " + validatable.getValue();

        validatable.error(this);
    }

    @Override
    public String getErrorMessage(IErrorMessageSource messageSource) {
        return errorMessage;
    }

    private String getString(String key){
        ResourceBundle resourceBundle = resourceBundleMap.get(locale.getLanguage());

        if (resourceBundle == null){
            resourceBundle = ResourceBundle.getBundle(RestrictionValidator.class.getName());
            resourceBundleMap.put(locale.getLanguage(), resourceBundle);
        }

        return resourceBundle.getString(key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration) {
        this.enumeration = enumeration;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(Integer totalDigits) {
        this.totalDigits = totalDigits;
    }

    public Long getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(Long minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Long getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(Long maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public boolean isRequiredRowNum() {
        return requiredRowNum;
    }

    public void setRequiredRowNum(boolean requiredRowNum) {
        this.requiredRowNum = requiredRowNum;
    }

    public Element getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(Element typeElement) {
        this.typeElement = typeElement;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
