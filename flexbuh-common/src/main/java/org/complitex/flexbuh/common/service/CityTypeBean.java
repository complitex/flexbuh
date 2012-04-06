package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.CityTypeFilter;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 12.01.12 9:26
 */
@Stateless
public class CityTypeBean extends AbstractBean {

    public List<CityType> getCityTypes(String start, Locale locale) {
        return sqlSession().selectList("selectCityTypes", new CityTypeFilter(0, 10, locale, start));
    }

}
