package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.Dictionary;
import org.complitex.flexbuh.service.AbstractBean;

import java.util.Date;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 26.08.11 14:50
 */
public abstract class DictionaryBean<T extends Dictionary> extends AbstractBean {
	public static final String NS = DictionaryBean.class.getName();

	public void delete(T dictionary) {
		sqlSession().delete(NS + ".delete", Stub.stub(dictionary));
	}

	public void delete(Date uploadDate) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("uploadDate", uploadDate);
		sqlSession().delete(NS + ".deleteByUploadDate", params);
	}

	abstract public void create(T dictionary);

	abstract protected String getTable();
}
