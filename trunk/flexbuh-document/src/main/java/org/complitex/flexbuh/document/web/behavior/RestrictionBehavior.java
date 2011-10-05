package org.complitex.flexbuh.document.web.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.value.IValueMap;
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
public class RestrictionBehavior extends Behavior{
    private final static List<String> NUMBER_BASE = Arrays.asList("xs:nonNegativeInteger", "xs:integer", "xs:unsignedLong");
    private final static Map<String, ResourceBundle> resourceBundleMap = new ConcurrentHashMap<>();

    private final static String COLOR_ERROR = "#F5A9A9";
    private final static String COLOR_VALIDATED = "#BCF5A9";
    private final static String COLOR_DEFAULT = "#cccccc";

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
    private Locale locale;
    private String errorMessage;
    private String title;

    private String defaultColor;

    public RestrictionBehavior(Element typeElement, Locale locale, String title) {
        this.typeElement = typeElement;
        this.locale = locale;
        this.title = title;

        if (typeElement != null) {
            parse();
        }
    }

    private void parse(){
        name = typeElement.getAttribute("name");

        NodeList documentationNodeList = typeElement.getElementsByTagName("xs:documentation");
        if (documentationNodeList.getLength() > 0){
            documentation = documentationNodeList.item(0).getTextContent();
        }

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

    public void validate(String value) {
        errorMessage = null;

        if (length != null && value.length() != length){
            error("length", length);
        }

        if (minLength != null && value.length() < minLength){
            error("minLength", minLength);
        }

        if (maxLength != null && value.length() > maxLength){
            error("maxLength", maxLength);
        }

        if (totalDigits != null && value.length() == totalDigits){
            error("totalDigits", totalDigits);
        }

        if (pattern != null && !pattern.matcher(value).matches()){
            error("pattern", pattern);
        }

        if (enumeration.size() > 0 && !enumeration.contains(value)){
            error("enumeration", null);
        }

        if (NUMBER_BASE.contains(base)){
            try {
                Long number = Long.valueOf(value);

                if (minInclusive != null && number < minInclusive){
                    error("minInclusive", minInclusive);
                }

                if (maxInclusive != null && number > maxInclusive){
                    error("maxInclusive", maxInclusive);
                }

            } catch (NumberFormatException e) {
                error("notNumber", null);
            }
        }
    }

    private void error(String key, Object argument){
        errorMessage = (argument != null ? MessageFormat.format(getString(key), argument) : getString(key)) +
                (documentation != null ? " \n" + documentation : "");
    }

    private String getString(String key){
        ResourceBundle resourceBundle = resourceBundleMap.get(locale.getLanguage());

        if (resourceBundle == null){
            resourceBundle = ResourceBundle.getBundle(RestrictionBehavior.class.getName());
            resourceBundleMap.put(locale.getLanguage(), resourceBundle);
        }

        return resourceBundle.getString(key);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (component instanceof FormComponent){
            FormComponent formComponent = (FormComponent) component;
            IValueMap attributes = tag.getAttributes();

            String value = formComponent.getValue();

            if (value != null && !value.isEmpty()) {
                validate(value);

                if (errorMessage != null){
                    attributes.put("style", "background-color: " + COLOR_ERROR);
                    attributes.put("title", errorMessage);
                }else {
                    attributes.put("style", "background-color: " + COLOR_VALIDATED);
                    attributes.put("title", title);
                }
            }else{
                attributes.put("style", "background-color: " + (defaultColor != null ? defaultColor : COLOR_DEFAULT));
                attributes.put("title", title);
            }
        }
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }
}
