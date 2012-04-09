package org.complitex.flexbuh.common.service.organization;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.organization.Organization;
import org.complitex.flexbuh.common.entity.organization.OrganizationFilter;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:54
 */
@Stateless
public class OrganizationBean extends AbstractBean {

    public static final String NS = OrganizationBean.class.getName();

    private static final Logger log = LoggerFactory.getLogger(OrganizationBean.class);

    @EJB
    private OrganizationTypeBean organizationTypeBean;

    @Transactional
    public void save(Organization organization, Locale locale) {
        if (organization.getId() != null) {
            update(organization, locale);
        } else {
            create(organization, locale);
        }
    }

    @Transactional
    public void create(Organization organization, Locale locale) {
        organization.setVersion(1L);
        organizationTypeBean.create(organization.getType(), locale);
        sqlSession().insert(NS + ".insertOrganization", organization);
    }

    @Transactional
    public void update(Organization organization, Locale locale) {
        organizationTypeBean.create(organization.getType(), locale);
        sqlSession().update(NS + ".updateOrganizationNullCompletionDate", organization);
        sqlSession().update(NS + ".updateOrganization", organization);
    }

    @Transactional
    public void updateCompletionDate(Organization organization) {
        sqlSession().update(NS + ".updateOrganizationCompletionDate", organization);
    }

    @Transactional
    public void deleteOrganization(Organization organization) {
        organization.setDeleted(true);
        if (organization.getCompletionDate() == null) {
            organization.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".deleteOrganization", organization);
    }

    public Organization getOrganization(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", new Date());

        return (Organization) sqlSession().selectOne(NS + ".selectCurrentOrganizationById", params);
    }

    public List<Organization> getOrganizations(OrganizationFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        return sqlSession().selectList(NS + ".selectCurrentOrganizations", params);
    }

    public int getOrganizationsCount(OrganizationFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        return (Integer)sqlSession().selectOne(NS + ".selectCurrentOrganizationsCount", params);
    }

}
