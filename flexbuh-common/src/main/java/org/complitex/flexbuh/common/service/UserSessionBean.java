package org.complitex.flexbuh.common.service;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.12.11 16:07
 */
@Stateless
public class UserSessionBean extends AbstractBean{
    public String getSelectedPersonProfileName(Long sessionId){
        return (String) sqlSession().selectOne("selectSelectedPersonProfileName", sessionId);
    }
}
