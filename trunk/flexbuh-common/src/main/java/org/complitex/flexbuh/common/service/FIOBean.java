package org.complitex.flexbuh.common.service;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.complitex.flexbuh.common.entity.*;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 17.01.12 12:58
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class FIOBean extends AbstractBean {

    public static final String NS = FIOBean.class.getName();

    public static final int SIZE = 10;

    @Transactional
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createFIO(String firstName, String middleName, String lastName, Locale locale) {
        if (firstName != null && !isFirstNameExist(firstName, locale)) {
            FirstName fName = new FirstName();
            setName(fName, firstName, locale);
            sqlSession().insert(NS + ".insertFirstName", fName);
        }
        if (middleName != null && !isMiddleNameExist(middleName, locale)) {
            MiddleName mName = new MiddleName();
            setName(mName, middleName, locale);
            sqlSession().insert(NS + ".insertMiddleName", mName);
        }
        if (lastName != null && !isLastNameExist(lastName, locale)) {
            LastName lName = new LastName();
            setName(lName, lastName, locale);
            sqlSession().insert(NS + ".insertLastName", lName);

        }
    }

    @SuppressWarnings("unchecked")
    public List<FirstName> getFirstNames(String start, Locale locale) {
        return sqlSession().selectList(NS + ".selectFirstNames", new NameFilter(0, SIZE, locale, start));
    }

    @SuppressWarnings("unchecked")
    public List<MiddleName> getMiddleNames(String start, Locale locale) {
        return sqlSession().selectList(NS + ".selectMiddleNames", new NameFilter(0, SIZE, locale, start));
    }

    @SuppressWarnings("unchecked")
    public List<LastName> getLastNames(String start, Locale locale) {
        return sqlSession().selectList(NS + ".selectLastNames", new NameFilter(0, SIZE, locale, start));
    }

    @Transactional
    public boolean isFirstNameExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isFirstNameExist", new NameFilter(0, SIZE, locale, start));
    }

    @Transactional
    public boolean isMiddleNameExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isMiddleNameExist", new NameFilter(0, SIZE, locale, start));
    }

    @Transactional
    public boolean isLastNameExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isLastNameExist", new NameFilter(0, SIZE, locale, start));
    }

    private void setName(LocalizedDomainObject ldo, String name, Locale locale) {
        if (locale.getLanguage().equals("ru")) {
            ldo.setNameRu(name);
            return;
        }
        ldo.setNameUk(name);
    }

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
