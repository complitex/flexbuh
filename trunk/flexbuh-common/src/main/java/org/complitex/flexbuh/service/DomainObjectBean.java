package org.complitex.flexbuh.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.DomainObject;
import org.complitex.flexbuh.entity.Stub;

import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 15:10
 */
public abstract class DomainObjectBean<T extends DomainObject> extends AbstractBean {
	public void create(T o) {
		sqlSession().insert(getNameSpace() + ".create", o);
	}

	public void delete(T o) {
		sqlSession().delete(getNameSpace() + ".delete", Stub.stub(o));
	}

	@SuppressWarnings("unchecked")
	public T read(long id) {
		return (T) sqlSession().selectOne(getNameSpace() + ".findById", new Stub(id, getTable()));
	}

	@SuppressWarnings("unchecked")
	public List<T> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return sqlSession().selectList(getNameSpace() + ".readAll", params);
	}

	abstract protected String getNameSpace();

	abstract protected String getTable();
}
