package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.entity.Feedback;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.11.11 16:19
 */
@Stateless
public class FeedbackBean extends AbstractBean{

    public List<Feedback> getFeedbacks(int first, int count){
        return sqlSession().selectList("selectFeedbacks", new AbstractFilter(first, count));        
    }
    
    public Integer getFeedbacksCount(){
        return (Integer) sqlSession().selectOne("selectFeedbacksCount");
    }

    public void save(Feedback feedback){
        sqlSession().insert("insertFeedback", feedback);
    }
}
