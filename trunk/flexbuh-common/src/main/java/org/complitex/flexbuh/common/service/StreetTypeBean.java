package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.entity.StreetTypeFilter;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 12.01.12 9:26
 */
@Stateless
public class StreetTypeBean extends AbstractBean {

    @SuppressWarnings("unchecked")
    public List<StreetType> getStreetTypes(String start, Locale locale) {
        return sqlSession().selectList("selectStreetTypes", new StreetTypeFilter(0, 10, locale, start));
    }

}
