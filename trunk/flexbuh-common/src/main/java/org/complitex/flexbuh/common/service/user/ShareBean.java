package org.complitex.flexbuh.common.service.user;

import org.complitex.flexbuh.common.entity.user.Share;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.04.12 14:52
 */
@Stateless
public class ShareBean extends AbstractBean {
    public static final String NS = ShareBean.class.getName();

    public void save(Share share){
        sqlSession().insert(NS + ".insertShare", share);
    }

    public void delete(Share share){
        sqlSession().delete(NS + ".deleteShare", share);
    }

    public boolean isExist(Share share){
        return (Boolean) sqlSession().selectOne(NS + ".isShareExist", share);
    }
}
