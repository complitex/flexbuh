package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.service.AbstractBean;

import java.util.Date;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 25.06.12 16:10
 */
public abstract class TemporalDomainObjectBean<T extends TemporalDomainObject> extends AbstractBean {

    protected String NS;

    private TemporalDomainObjectBean() {
    }

    protected TemporalDomainObjectBean(String NS) {
        this.NS = NS;
    }

    public T getTDObject(Long id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", new Date());

        return sqlSession().selectOne(NS + ".selectCurrentTDObjectById", params);
    }

    public T getTDObject(long id, long version) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);

        return sqlSession().selectOne(NS + ".selectTDObjectByIdAndVersion", params);
    }

    public T getTDObjectLastInHistory(Long id) {
        return sqlSession().selectOne(NS + ".selectTDObjectLastInHistory", id);
    }

    public T getTDObjectPreviewInHistoryByField(Long id, Long version, String fieldName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);
        params.put("fieldName", fieldName);

        return sqlSession().selectOne(NS + ".selectTDObjectPreviewInHistoryByField", params);
    }

    public T getTDObjectNextInHistoryByField(Long id, Long version, String fieldName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);
        params.put("fieldName", fieldName);

        return sqlSession().selectOne(NS + ".selectTDObjectNextInHistoryByField", params);
    }

    abstract public void save(T object);

}
