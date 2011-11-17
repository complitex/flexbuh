package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:52
 */
@Stateless
public class CounterpartBean extends AbstractBean{
    public Counterpart getCounterpart(Long id){
        return (Counterpart) sqlSession().selectOne("selectCounterpart", id);
    }

    @SuppressWarnings("unchecked")
    public List<Counterpart>  getCounterparts(CounterpartFilter filter){
        return sqlSession().selectList("selectCounterparts", filter);
    }

    public Integer getCounterpartCount(CounterpartFilter filter){
        return (Integer) sqlSession().selectOne("selectCounterpartCount", filter);
    }

    public void save(Counterpart counterpart){
        if (counterpart.getId() == null){
            sqlSession().insert("insertCounterpart", counterpart);
        }else{
            sqlSession().update("updateCounterpart", counterpart);
        }
    }
    
    public void delete(Long id){
        sqlSession().delete("deleteCounterpart", id);
    }
}
