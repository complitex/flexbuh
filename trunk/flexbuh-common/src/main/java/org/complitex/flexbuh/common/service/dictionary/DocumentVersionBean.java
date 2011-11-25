package org.complitex.flexbuh.common.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersionFilter;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 17:46
 */
@Stateless
public class DocumentVersionBean extends AbstractBean {
    public static final String NS = DocumentVersionBean.class.getName();

    public void save(DocumentVersion documentVersion) {
        sqlSession().insert(NS + ".insertDocumentVersion", documentVersion);
    }

	public void update(DocumentVersion documentVersion) {
        sqlSession().update(NS + ".updateDocumentVersion", documentVersion);
    }

    public DocumentVersion getDocumentVersion(Long id) {
        return (DocumentVersion) sqlSession().selectOne(NS + ".selectDocumentVersion", id);
    }

	@SuppressWarnings("unchecked")
	public List<DocumentVersion> getDocumentVersionsByDocument(String cDoc, String cDocSub) {
		Map<String, String> params = Maps.newHashMap();
		params.put("cDoc", cDoc);
		params.put("cDocSub", cDocSub);
		return sqlSession().selectList(NS + ".selectDocumentVersionsByDocument");
	}

    @SuppressWarnings("unchecked")
    public List<DocumentVersion> getDocumentVersions(DocumentVersionFilter filter) {
        return (List<DocumentVersion>)sqlSession().selectList(NS + ".selectDocumentVersions", filter);
    }

    public Integer getDocumentVersionsCount(DocumentVersionFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectDocumentVersionsCount", filter);
    }
}
