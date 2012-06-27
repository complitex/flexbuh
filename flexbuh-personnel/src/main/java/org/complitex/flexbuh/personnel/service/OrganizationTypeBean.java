package org.complitex.flexbuh.personnel.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.LocalizedDomainObject;
import org.complitex.flexbuh.personnel.entity.OrganizationType;
import org.complitex.flexbuh.personnel.entity.OrganizationTypeFilter;
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
    public void create(String type) {
        if (type != null && !isOrganizationTypeExist(type)) {
            sqlSession().insert(NS + ".insertOrganizationType", type);
        }
    }

    public List<OrganizationType> getOrganizationTypes(String start) {
        return sqlSession().selectList(NS + ".selectOrganizationTypes", FilterWrapper.of(new OrganizationType(start), 0, SIZE));
    }

    public boolean isOrganizationTypeExist(String start) {
        return (Boolean)sqlSession().selectOne(NS + ".isOrganizationTypeExist", start);
    }
}
