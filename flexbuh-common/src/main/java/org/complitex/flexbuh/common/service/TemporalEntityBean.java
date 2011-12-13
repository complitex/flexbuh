package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;
import org.complitex.flexbuh.common.entity.TemporalEntityFilter;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 15:53
 */
@Stateless
public class TemporalEntityBean extends AbstractBean{
    public void save(AbstractTemporalEntity entity){
        sqlSession().insert("insertTemporalEntity", entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractTemporalEntity> List<T> getTemporalEntities(TemporalEntityFilter<T> filter){
        return sqlSession().selectList("selectTemporalEntities", filter);
    }
}
