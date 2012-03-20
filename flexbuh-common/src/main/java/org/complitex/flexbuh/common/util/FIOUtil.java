package org.complitex.flexbuh.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.03.12 12:34
 */
public class FIOUtil {
    public static String getFIO(String lastName, String firstName, String middleName) {
        return (lastName != null? lastName: "")
                + " " +
                (firstName != null? firstName: "")
                + " " +
                (middleName != null? middleName: "");
    }

    public static String getLastName(String fio) {
        String[] resultSplit = fio.split(" ", 2);
        return resultSplit.length > 0 && StringUtils.isNotEmpty(resultSplit[0])? resultSplit[0]: null;
    }

    public static String getFirstName(String fio) {
        String[] resultSplit = fio.split(" ", 3);
        return resultSplit.length > 1 && StringUtils.isNotEmpty(resultSplit[1])? resultSplit[1]: null;
    }

    public static String getMiddleName(String fio) {
        String[] resultSplit = fio.split(" ", 3);
        return resultSplit.length > 2 && StringUtils.isNotEmpty(resultSplit[2])? resultSplit[2]: null;
    }
}
