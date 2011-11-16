package org.complitex.flexbuh.common.service.user;

import org.complitex.flexbuh.common.entity.user.Session;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 15:49
 */
@Stateless
public class SessionBean extends AbstractBean {
	public static final String NS = SessionBean.class.getName();

	public Session getSessionByCookie(String cookie) {
		return (Session)sqlSession().selectOne(NS+ ".selectSessionByCookie", cookie);
	}

	public void create(Session session) {
		sqlSession().insert(NS + ".insertSession", session);
	}
}
