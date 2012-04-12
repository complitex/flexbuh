package org.complitex.flexbuh.common.service.organization;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 11.04.12 14:40
 */
@Stateless
public class OrganizationBaseBean extends AbstractBean {
    public static final String NS = OrganizationBaseBean.class.getName();

    private static final Logger log = LoggerFactory.getLogger(OrganizationBaseBean.class);

    @Transactional
    public void editUserOrganizationList(List<OrganizationBase> organizations, User user) {
        List<OrganizationBase> dbOrganizations = getUserOrganizations(user);

        Collections.sort(dbOrganizations);
        Collections.sort(organizations);
        log.debug("Db organizations: {}", dbOrganizations);
        log.debug("New organizations: {}", organizations);
        Map<String, Object> organization = Maps.newHashMap();
        organization.put("login", user.getLogin());
        int i = 0, k = 0, j;
        while (k < dbOrganizations.size()) {

            OrganizationBase dbUserOrganization = dbOrganizations.get(k);

            j = -1;
            while (i < organizations.size()) {
                OrganizationBase userOrganization = organizations.get(i);
                j = dbUserOrganization.compareTo(userOrganization);
                if (j < 0) {
                    organization.put("organizationId", dbUserOrganization.getId());
                    log.debug("Delete user organization: {}", dbUserOrganization.getId());
                    sqlSession().delete(NS + ".deleteUserOrganization", organization);
                    break;
                } else if (j > 0) {
                    organization.put("organizationId", userOrganization.getId());
                    log.debug("Insert user organization: {}", userOrganization.getId());
                    sqlSession().insert(NS + ".insertUserOrganization", organization);
                } else {
                    log.debug("Equals: {}", userOrganization.getId());
                    i++;
                    break;
                }
                i++;
            }
            if (i >= organizations.size() && j != 0) {
                break;
            }
            k++;
        }

        while (k < dbOrganizations.size()) {
            organization.put("organizationId", dbOrganizations.get(k).getId());
            log.debug("Delete user organization: {}", dbOrganizations.get(k).getId());
            sqlSession().delete(NS + ".deleteUserOrganization", organization);
            k++;
        }

        while (i < organizations.size()) {
            organization.put("organizationId", organizations.get(i).getId());
            log.debug("Insert user organization: {}", organizations.get(i).getId());
            sqlSession().delete(NS + ".insertUserOrganization", organization);
            i++;
        }
    }

    public List<OrganizationBase> getUserOrganizations(User user) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("login", user.getLogin());
        params.put("currentDate", new Date());
        return sqlSession().selectList(NS + ".selectUserOrganizations", params);
    }

    public List<OrganizationBase> getAllOrganizations() {
        return sqlSession().selectList(NS + ".selectCurrentOrganizations", new Date());
    }

}
