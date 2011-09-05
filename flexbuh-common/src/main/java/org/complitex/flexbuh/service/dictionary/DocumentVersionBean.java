package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.entity.dictionary.NormativeDocumentName;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 17:46
 */
@Stateless
public class DocumentVersionBean extends DictionaryBean<DocumentVersion> {
	public static final String NS = DocumentVersionBean.class.getName();

	@Override
    public void create(DocumentVersion documentVersion) {
        sqlSession().insert(NS + ".create", documentVersion);
		Map<String, Object> params = Maps.newHashMap();
		params.put("documentVersionId", documentVersion.getId());
		for (NormativeDocumentName normativeDocumentName : documentVersion.getNormativeDocumentNames()) {
			params.put("languageId", normativeDocumentName.getLanguage().getId());
			params.put("val", normativeDocumentName.getValue());
			sqlSession().insert(NormativeDocumentName.class.getName() + ".create", params);
		}
    }

	@SuppressWarnings("unchecked")
	public List<DocumentVersion> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<DocumentVersion>)sqlSession().selectList(NS + ".readAll", params);
	}

	public DocumentVersion read(long id) {
		return (DocumentVersion)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}


	@Override
	public String getTable() {
		return DocumentVersion.TABLE;
	}
}
