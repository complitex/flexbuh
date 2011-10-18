package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.user.User;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 14:56
 */
@Stateless
public class UserBean extends AbstractBean{
	public static final String NS = UserBean.class.getName();

	@EJB
	private SessionBean sessionBean;

	public User getUser(long id) {
		return (User) sqlSession().selectOne(NS + ".selectUser", id);
	}

    public User getUserBySessionId(Long sessionId){
        return (User)sqlSession().selectOne(NS + ".selectUserBySessionId", sessionId);
    }
}