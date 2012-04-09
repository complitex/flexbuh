package org.complitex.flexbuh.common.service.organization;

import org.complitex.flexbuh.common.entity.LocalizedDomainObject;
import org.complitex.flexbuh.common.entity.organization.OrganizationType;
import org.complitex.flexbuh.common.entity.organization.OrganizationTypeFilter;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 14:08
 */
@Stateless
public class OrganizationTypeBean extends AbstractBean {

    public static final String NS = OrganizationTypeBean.class.getName();

    public static final int SIZE = 10;

    @Transactional
    public void create(String type, Locale locale) {
        if (type != null && !isOrganizationTypeExist(type, locale)) {
            OrganizationType organizationType = new OrganizationType();
            setName(organizationType, type, locale);
            sqlSession().insert(NS + ".insertOrganizationType", organizationType);
        }
    }

    public List<OrganizationType> getOrganizationTypes(String start, Locale locale) {
        return sqlSession().selectList(NS + ".selectOrganizationTypes", new OrganizationTypeFilter(0, SIZE, locale, start));
    }

    public boolean isOrganizationTypeExist(String start, Locale locale) {
        return (Boolean)sqlSession().selectOne(NS + ".isOrganizationTypeExist", new OrganizationTypeFilter(0, SIZE, locale, start));
    }

    private void setName(LocalizedDomainObject ldo, String name, Locale locale) {
        if (locale.getLanguage().equals("ru")) {
            ldo.setNameRu(name);
            return;
        }
        ldo.setNameUk(name);
    }
}
