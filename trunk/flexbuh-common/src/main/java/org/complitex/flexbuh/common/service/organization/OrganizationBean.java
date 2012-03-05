package org.complitex.flexbuh.common.service.organization;

import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.complitex.flexbuh.common.entity.organization.Organization;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 16.02.12 17:54
 */
@Stateless
public class OrganizationBean extends AbstractBean {

    public static final String NS = OrganizationBean.class.getName();

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
        sqlSession().insert(NS + ".insertOrganization", organization);
    }

    @Transactional
    public void update(Organization organization) {
        sqlSession().update(NS + ".updateOrganization", organization);
    }

    public Organization getOrganization(Long id) {
        return (Organization) sqlSession().selectOne(NS + ".selectOrganizationById", id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Organization> getOrganizations() {
        return sqlSession().selectList(NS + ".selectAllOrganizations");
    }

}
