package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AddressBean;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.OrganizationFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectHistoryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:54
 */
@Stateless
public class OrganizationBean extends TemporalDomainObjectBean<Organization> {

    public static final String NS = OrganizationBean.class.getName();

    private static final Logger log = LoggerFactory.getLogger(OrganizationBean.class);

    @EJB
    private OrganizationTypeBean organizationTypeBean;

    @EJB
    private AddressBean addressBean;

    public OrganizationBean() {
        super(NS);
    }

    @Transactional
    public void save(Organization organization) {
        if (organization.getId() != null) {
            update(organization);
        } else {
            create(organization);
        }
    }

    @Transactional
    public void create(Organization organization) {
        organization.setPhysicalAddress(addressBean.create(organization.getPhysicalAddress()));
        organization.setJuridicalAddress(addressBean.create(organization.getJuridicalAddress()));
        organization.setVersion(1L);
        organizationTypeBean.create(organization.getType());
        sqlSession().insert(NS + ".insertOrganization", organization);
    }

    @Transactional
    public void update(Organization organization) {
        organization.setPhysicalAddress(addressBean.create(organization.getPhysicalAddress()));
        organization.setJuridicalAddress(addressBean.create(organization.getJuridicalAddress()));
        organizationTypeBean.create(organization.getType());
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

    @Override
    @NotNull
    public <A extends TemporalDomainObjectFilter> List<Organization> getTDOObjects(@Null A filter) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        return sqlSession().selectList(NS + ".selectCurrentOrganizations", params);
    }

    public int getOrganizationsCount(OrganizationFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        return sqlSession().selectOne(NS + ".selectCurrentOrganizationsCount", params);
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

        return sqlSession().selectOne(NS + ".selectOrganizationHistoryCount", params);
    }
}
