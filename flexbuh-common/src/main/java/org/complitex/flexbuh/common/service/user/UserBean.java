package org.complitex.flexbuh.common.service.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.complitex.flexbuh.common.security.SecurityRole.*;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 14:56
 */
@Stateless
public class UserBean extends AbstractBean {
	public static final String NS = UserBean.class.getName();

    private static final List<String> ALL_ROLES = Lists.newArrayList(
            AUTHORIZED,
            ADMIN_MODULE_EDIT,
            LOG_VIEW);

    private static final Map<String, List<String>> GROUPS = Maps.newHashMap();

    static {
        GROUPS.put("ADMINISTRATORS", Lists.<String>newArrayList(AUTHORIZED, ADMIN_MODULE_EDIT, LOG_VIEW));
        GROUPS.put("EMPLOYEES", Lists.<String>newArrayList(AUTHORIZED));

        ALL_ROLES.addAll(GROUPS.keySet());
    }

    @Transactional
    public void create(User user) {
        user.setPassword(DigestUtils.md5Hex(user.getLogin())); //md5 password

        sqlSession().insert(NS + ".insertUser", user);

        //сохранение привилегий
        Map<String, String> newRole = Maps.newHashMap();
        newRole.put("login", user.getLogin());
        for(String role : user.getRoles()){
            newRole.put("role", role);
            sqlSession().insert(NS + ".insertUserGroup", newRole);
        }
    }

    @Transactional
    public void update(User user) {
        User dbUser = (User) sqlSession().selectOne(NS + ".selectUser", user.getId());

        //удаление групп привилегий
        Collections.sort(user.getRoles());
        Map<String, String> role = Maps.newHashMap();
        role.put("login", user.getLogin());
        int i = 0, j = -1;
        for (int k = 0 ; k < dbUser.getRoles().size(); k++) {

            String dbUserRole = dbUser.getRoles().get(k);

            while (i < user.getRoles().size() || j == 0) {
                j = dbUserRole.compareTo(user.getRoles().get(i));
                role.put("role", dbUserRole);
                if (j < 0) {
                    sqlSession().delete(NS + ".deleteUserRole", role);
                    if (k < dbUser.getRoles().size()) {
                        break;
                    }
                } else if (j > 0) {
                    sqlSession().delete(NS + ".insertUserRole", role);
                }
                i++;
            }

            j = -1;
        }
    }

	public User getUser(long id) {
		return (User) sqlSession().selectOne(NS + ".selectUserById", id);
	}

    public User getUserBySessionId(Long sessionId) {
        return (User)sqlSession().selectOne(NS + ".selectUserBySessionId", sessionId);
    }

    @SuppressWarnings({"unchecked"})
    public List<User> getUsers(UserFilter filter) {
        appendRoleGroup(filter);
        return sqlSession().selectList(NS + ".selectUsers", filter);
    }

    public int getUsersCount(UserFilter filter) {
        appendRoleGroup(filter);
        return (Integer) sqlSession().selectOne(NS + ".selectUsersCount", filter);
    }

    private void appendRoleGroup(UserFilter filter) {
        String role = filter.getRole();
        for (Map.Entry<String, List<String>> group : GROUPS.entrySet()) {
            if (group.getValue().contains(role)) {
                filter.appendRole(group.getKey());
            }
        }
    }

    public List<String> getAllRoles() {
        return ALL_ROLES;
    }
}