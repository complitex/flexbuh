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
public class Restriction<T> implements INullAcceptingValidator<T>, IValidationError {
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

    public Restriction(Element typeElement) {
        this(typeElement, Locale.getDefault());
    }

    public Restriction(Element typeElement, Locale locale) {
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

            if (pattern != null && !pattern.matcher(valueString).matches()){
                error(validatable, "pattern");
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

            if (length != null && valueString.length() == length){
                error(validatable, "length");
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

    private void error(IValidatable validatable, String key){
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
        validatable.error(this);
    }

    @Override
    public String getErrorMessage(IErrorMessageSource messageSource) {
        return errorMessage;
    }

    private String getString(String key){
        ResourceBundle resourceBundle = resourceBundleMap.get(locale.getLanguage());

        if (resourceBundle == null){
            resourceBundle = ResourceBundle.getBundle(Restriction.class.getName());
            resourceBundleMap.put(locale.getLanguage(), resourceBundle);
        }

        return resourceBundle.getString(key);
    }
}
