package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.DocumentTerm;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 10:05
 */
@Stateless
public class DocumentTermBean extends DictionaryBean<DocumentTerm> {

	public static final String NS = DocumentTermBean.class.getName();

	@Override
    public void create(DocumentTerm documentTerm) {
        sqlSession().insert(NS + ".create", documentTerm);
    }

	@SuppressWarnings("unchecked")
	public List<DocumentTerm> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<DocumentTerm>)sqlSession().selectList(NS + ".readAll", params);
	}

	public DocumentTerm read(long id) {
		return (DocumentTerm)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}

	@Override
	protected String getTable() {
		return DocumentTerm.TABLE;
	}
}
