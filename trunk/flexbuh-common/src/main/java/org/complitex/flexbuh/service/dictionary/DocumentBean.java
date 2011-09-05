package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentName;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 22.08.11 12:15
 */
@Stateless
public class DocumentBean extends DictionaryBean<Document> {
    public static final String NS = DocumentBean.class.getName();

	@Override
    public void create(Document document) {
        sqlSession().insert(NS + ".create", document);
		Map<String, Object> params = Maps.newHashMap();
		params.put("documentId", document.getId());
		for (DocumentName documentName : document.getNames()) {
			params.put("languageId", documentName.getLanguage().getId());
			params.put("val", documentName.getValue());
			sqlSession().insert(DocumentName.class.getName() + ".create", params);
		}
    }

	@SuppressWarnings("unchecked")
	public List<Document> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<Document>)sqlSession().selectList(NS + ".readAll", params);
	}

	public Document read(long id) {
		return (Document)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}

	@Override
	public String getTable() {
		return Document.TABLE;
	}
}
