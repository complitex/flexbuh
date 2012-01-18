package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.*;
import org.complitex.flexbuh.common.mybatis.Transactional;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 17.01.12 12:58
 */
@Stateless
public class FIOBean extends AbstractBean {

    public static final String NS = FIOBean.class.getName();

    private static final int SIZE = 10;

    @Transactional
    public void createFIO(String firstName, String middleName, String lastName, Locale locale) {
        if (firstName != null && !isFirstNameExist(firstName, locale)) {
            FirstName fName = new FirstName();
            setName(fName, firstName, locale);
            sqlSession().insert(NS + ".insertFirstName", fName);
        }
        if (middleName != null && !isMiddleNameExist(firstName, locale)) {
            MiddleName mName = new MiddleName();
            setName(mName, middleName, locale);
            sqlSession().insert(NS + ".insertMiddleName", mName);
        }
        if (lastName != null && !isLastNameExist(firstName, locale)) {
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

    public boolean isFirstNameExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isFirstNameExist", new NameFilter(0, SIZE, locale, start));
    }

    public boolean isMiddleNameExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isMiddleNameExist", new NameFilter(0, SIZE, locale, start));
    }

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
}
