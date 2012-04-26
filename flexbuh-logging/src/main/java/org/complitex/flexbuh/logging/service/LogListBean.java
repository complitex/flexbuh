package org.complitex.flexbuh.logging.service;

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
	public static final String NS = LogListBean.class.getCanonicalName();

    public List<Log> getLogs(LogFilter filter){
        return sqlSession().selectList(NS + ".selectLogs", filter);
    }

    public int getLogsCount(LogFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectLogsCount", filter);
    }

    public List<String> getModules(){
        return sqlSession().selectList(NS + ".selectModules");
    }

    public List<String> getControllers(){
        return sqlSession().selectList(NS + ".selectControllers");
    }

    public List<String> getModels(){
        return sqlSession().selectList(NS + ".selectModels");
    }

}
