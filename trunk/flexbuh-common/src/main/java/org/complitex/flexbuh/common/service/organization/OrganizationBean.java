package org.complitex.flexbuh.common.service.organization;

import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.complitex.flexbuh.common.entity.organization.Organization;
import org.complitex.flexbuh.common.entity.organization.OrganizationFilter;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:54
 */
@Stateless
public class OrganizationBean extends AbstractBean {

    public static final String NS = OrganizationBean.class.getName();

    private static final Logger log = LoggerFactory.getLogger(OrganizationBean.class);

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
        organization.setVersion(1L);
        sqlSession().insert(NS + ".insertOrganization", organization);
    }

    @Transactional
    public void update(Organization organization) {
        sqlSession().update(NS + ".updateOrganizationNullCompletionDate", organization);
        sqlSession().update(NS + ".updateOrganization", organization);
    }

    @Transactional
    public void updateCompletionDate(Organization organization) {
        sqlSession().update(NS + ".updateOrganizationCompletionDate", organization);
    }

    public Organization getOrganization(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", new Date());

        return (Organization) sqlSession().selectOne(NS + ".selectCurrentOrganizationById", params);
    }

    @SuppressWarnings({"unchecked"})
    public List<Organization> getOrganizations(OrganizationFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        return sqlSession().selectList(NS + ".selectCurrentOrganizations", params);
    }

    @SuppressWarnings({"unchecked"})
    public int getOrganizationsCount(OrganizationFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("filter", filter);
        params.put("currentDate", new Date());

        Integer result = (Integer)sqlSession().selectOne(NS + ".selectCurrentOrganizationsCount", params);
        log.debug("getOrganizationsCount={}", result);
        return  result;
    }

}
