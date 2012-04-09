package org.complitex.flexbuh.logging.service;

import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.logging.entity.Log;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.11.11 17:18
 */
@Stateless
public class LogListBean extends AbstractBean {
	public static final String STATEMENT_PREFIX = LogListBean.class.getCanonicalName();

    @SuppressWarnings({"unchecked"})
    @Transactional
    public List<Log> getLogs(LogFilter filter){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectLogs", filter);
    }

    @Transactional
    public int getLogsCount(LogFilter filter){
        return (Integer) sqlSession().selectOne(STATEMENT_PREFIX + ".selectLogsCount", filter);
    }

    @Transactional
    public List<String> getModules(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectModules");
    }

    @Transactional
    public List<String> getControllers(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectControllers");
    }

    @Transactional
    public List<String> getModels(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectModels");
    }

}
