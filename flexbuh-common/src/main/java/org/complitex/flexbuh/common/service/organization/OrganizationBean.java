package org.complitex.flexbuh.common.service.organization;

import com.google.common.collect.Maps;
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
    public void save(User user) {
        if (user.getId() != null) {
            update(user);
        } else {
            create(user);
        }
    }

    @Transactional
    public void create(User user) {
        sqlSession().insert(NS + ".insertOrganization", user);

        //сохранение организаций пользователя
        Map<String, String> newRole = Maps.newHashMap();
        newRole.put("login", user.getLogin());
        for(String role : user.getRoles()){
            newRole.put("role", role);
            sqlSession().insert(NS + ".insertUserRole", newRole);
        }
    }

    @Transactional
    public void update(User user) {
        sqlSession().update(NS + ".updateOrganization", user);
    }

    public Organization getOrganization(Long id) {
        return (Organization) sqlSession().selectOne(NS + ".selectOrganizationById", id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Organization> getOrganizations() {
        return sqlSession().selectList(NS + ".selectAllOrganizations");
    }

}
