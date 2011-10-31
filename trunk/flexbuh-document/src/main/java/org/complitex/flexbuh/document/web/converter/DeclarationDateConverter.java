package org.complitex.flexbuh.document.web.converter;

import org.apache.wicket.util.convert.IConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.10.11 17:21
 */
public class DeclarationDateConverter implements IConverter<String>{
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");
    
    @Override
    public String convertToObject(String value, Locale locale) {
        try {
            Date date = DATE_FORMAT.parse(value);
            
            return XML_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return value;
        }
    }

    @Override
    public String convertToString(String value, Locale locale) {
        try {
            Date date = XML_DATE_FORMAT.parse(value);

            return DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return value;
        }
    }
}
