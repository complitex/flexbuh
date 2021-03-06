package org.complitex.flexbuh.common.service.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.complitex.flexbuh.common.entity.user.User;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.AddressBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.security.Principal;
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

    private static final Logger log = LoggerFactory.getLogger(UserBean.class);

    private static final List<String> ALL_ROLES = Lists.newArrayList(
            /*
            AUTHORIZED,
            ADMIN_MODULE_EDIT,
            LOG_VIEW,
            PERSONAL_MANAGER
            */
            );

    private static final Map<String, List<String>> GROUPS = Maps.newHashMap();

    static {
        GROUPS.put("ADMINISTRATORS", Lists.<String>newArrayList(AUTHORIZED, ADMIN_MODULE_EDIT, DOCUMENT_VIEW_ALL_SESSION, LOG_VIEW, PERSONAL_MANAGER));
        GROUPS.put("EMPLOYEES", Lists.<String>newArrayList(AUTHORIZED));
        GROUPS.put("PERSONAL_MANAGER", Lists.<String>newArrayList(AUTHORIZED, PERSONAL_MANAGER));

        ALL_ROLES.addAll(GROUPS.keySet());
    }

    private static final String ANONYMOUS_USER_LOGIN_NAME = "ANONYMOUS";

    @Resource
    private SessionContext  sessionContext;

    @EJB
    private AddressBean addressBean;

    public User getCurrentUser() {
        Principal principal = sessionContext.getCallerPrincipal();
        return principal != null? getUser(principal.getName()): null;
    }

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
        user.setAddress(addressBean.create(user.getAddress()));
        user.setPassword(DigestUtils.md5Hex(user.getPassword())); //md5 password

        sqlSession().insert(NS + ".insertUser", user);

        // сохранение привилегий
        Map<String, String> newRole = Maps.newHashMap();
        // минимальные привилегии
        newRole.put("login", user.getLogin());
        newRole.put("role", SecurityRole.AUTHORIZED);
        sqlSession().insert(NS + ".insertUserRole", newRole);
        for(String role : user.getRoles()) {
            newRole.put("role", role);
            sqlSession().insert(NS + ".insertUserRole", newRole);
        }
    }

    @Transactional
    public void update(User user) {
        user.setAddress(addressBean.create(user.getAddress()));

        User dbUser = (User) sqlSession().selectOne(NS + ".selectUserById", user.getId());
        if (user.getPassword() != null) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        }

        log.debug("Db roles: {}", dbUser.getRoles());
        log.debug("New roles: {}", user.getRoles());

        //удаление групп привилегий
        Collections.sort(user.getRoles());
        Map<String, String> role = Maps.newHashMap();
        role.put("login", user.getLogin());
        int i = 0, k = 0, j;
        while (k < dbUser.getRoles().size()) {

            String dbUserRole = dbUser.getRoles().get(k);

            j = -1;
            while (i < user.getRoles().size()) {
                String userRole = user.getRoles().get(i);
                j = dbUserRole.compareTo(userRole);
                if (j < 0) {
                    role.put("role", dbUserRole);
                    sqlSession().delete(NS + ".deleteUserRole", role);
                    break;
                } else if (j > 0) {
                    role.put("role", userRole);
                    sqlSession().insert(NS + ".insertUserRole", role);
                } else {
                    i++;
                    break;
                }
                i++;
            }
            if (i >= user.getRoles().size() && j != 0) {
                break;
            }
            k++;
        }

        while (k < dbUser.getRoles().size()) {
            role.put("role", dbUser.getRoles().get(k));
            sqlSession().delete(NS + ".deleteUserRole", role);
            k++;
        }

        while (i < user.getRoles().size()) {
            role.put("role", user.getRoles().get(i));
            sqlSession().delete(NS + ".insertUserRole", role);
            i++;
        }

        sqlSession().update(NS + ".updateUser", user);
    }

	public User getUser(long id) {
		return (User) sqlSession().selectOne(NS + ".selectUserById", id);
	}

    public User getUser(String login) {
		return (User) sqlSession().selectOne(NS + ".selectUserByLogin", login);
	}

    public User getUserBySessionId(Long sessionId) {
        return (User)sqlSession().selectOne(NS + ".selectUserBySessionId", sessionId);
    }

    public List<User> getUsers(UserFilter filter) {
        appendRoleGroup(filter);
        return sqlSession().selectList(NS + ".selectUsers", filter);
    }

    public int getUsersCount(UserFilter filter) {
        appendRoleGroup(filter);
        return (Integer) sqlSession().selectOne(NS + ".selectUsersCount", filter);
    }

    public boolean isLoginExist(String login) {
        return (Boolean)sqlSession().selectOne(NS + ".isLoginExist", login);
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

    public boolean isPersonalManager(User user) {
        for (String role : user.getRoles()) {
            if (role.equals(PERSONAL_MANAGER) || (GROUPS.containsKey(role) && GROUPS.get(role).contains(PERSONAL_MANAGER))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check user
     *
     * @param user User object
     * @return <code>true</code> if user is anonymous/null otherwise <code>false</code>
     */
    public boolean isAnonymous(User user) {
        return user == null || ANONYMOUS_USER_LOGIN_NAME.equals(user.getLogin());
    }

    public boolean isAnonymous(){
        return isAnonymous(getCurrentUser());
    }

    public List<User> getSharedUsers(Long sessionId){
        return sqlSession().selectList(NS + ".selectSharedUsers", sessionId);
    }

    public User findUser(String s){
        return (User) sqlSession().selectOne(NS + ".findUser", s);
    }
}