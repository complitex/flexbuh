package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.entity.dictionary.NormativeDocumentName;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 17:46
 */
@Stateless
public class DocumentVersionBean extends AbstractBean {
    public static final String NS = DocumentVersionBean.class.getName();

    public void save(DocumentVersion documentVersion) {
        sqlSession().insert(NS + ".insertDocumentVersion", documentVersion);

        for (NormativeDocumentName normativeDocumentName : documentVersion.getNormativeDocumentNames()) {
            normativeDocumentName.setDocumentVersionId(documentVersion.getId());

            sqlSession().insert(NS + ".insertNormativeDocumentName", normativeDocumentName);
        }
    }

    public DocumentVersion getDocumentVersion(Long id) {
        return (DocumentVersion) sqlSession().selectOne(NS + ".selectDocumentVersion", id);
    }

    @SuppressWarnings("unchecked")
    public List<DocumentVersion> getDocumentVersions() {
        return (List<DocumentVersion>)sqlSession().selectList(NS + ".selectAllDocumentVersions");
    }

    public Integer getDocumentVersionsCount(){
        return (Integer) sqlSession().selectOne(NS + ".selectAllDocumentVersionsCount");
    }

    @SuppressWarnings("unchecked")
    public List<DocumentVersion> getDocumentVersions(int first, int count) {
        return sqlSession().selectList(NS + ".selectDocumentVersions", new AbstractFilter(first, count));
    }
}
