package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.OrganizationFilter;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectHistoryFilter;
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

    public Organization getOrganization(long id, long version) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);

        return (Organization) sqlSession().selectOne(NS + ".selectOrganizationByIdAndVersion", params);
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

    public List<Organization> getOrganizationHistory(Long id, TemporalDomainObjectHistoryFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("filter", filter);

        return sqlSession().selectList(NS + ".selectOrganizationHistory", params);
    }

    public int getOrganizationHistoryCount(Long id, TemporalDomainObjectHistoryFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("filter", filter);

        return (Integer)sqlSession().selectOne(NS + ".selectOrganizationHistoryCount", params);
    }

    public Organization getOrganizationLastInHistory(Long id) {
        return sqlSession().selectOne(NS + ".selectOrganizationLastInHistory", id);
    }

}
