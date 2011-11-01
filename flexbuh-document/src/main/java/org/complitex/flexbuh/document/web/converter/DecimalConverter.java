package org.complitex.flexbuh.document.web.converter;

import org.apache.wicket.util.convert.IConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.11 14:40
 */
public class DecimalConverter implements IConverter<String>{
    private DecimalFormat decimalFormat;
    
    public DecimalConverter(int scale) {
        String pattern = "#.";
        
        for (int i = 0; i < scale; ++i){
            pattern += "0";
        }

        decimalFormat = new DecimalFormat(pattern);
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    @Override
    public String convertToObject(String value, Locale locale) {
        try {
            Double d = Double.valueOf(value);

            return decimalFormat.format(d);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    @Override
    public String convertToString(String value, Locale locale) {
        return value;
    }
}
