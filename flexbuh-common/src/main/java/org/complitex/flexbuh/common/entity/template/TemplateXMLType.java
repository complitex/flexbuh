package org.complitex.flexbuh.common.entity.template;

import java.util.regex.Pattern;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 20.04.12 15:33
*/
public enum TemplateXMLType {
    FO("template_fo", "fo", "\\w{6}\\d{2}.[Ff][Oo]"),
    XSD("template_xsd", "xsd", "((\\w{6}\\d{2})|(common_types)).[Xx][Ss][Dd]"),
    XSL("template_xsl", "xsl", "\\w{6}\\d{2}.[Xx][Ss][Ll]"),
    CONTROL("template_control", "controls", "\\w{6}\\d{2}.[Xx][Mm][Ll]");

    private String table;
    private String subDir;
    private Pattern pattern;

    TemplateXMLType(String table, String subDir, String pattern) {
        this.table = table;
        this.subDir = subDir;
        this.pattern = Pattern.compile(pattern);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSubDir() {
        return subDir;
    }

    public void setSubDir(String subDir) {
        this.subDir = subDir;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
