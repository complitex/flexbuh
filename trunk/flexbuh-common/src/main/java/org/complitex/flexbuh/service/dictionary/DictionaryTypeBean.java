package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 17.08.11 12:40
 */
@Stateless
public class DictionaryTypeBean extends AbstractBean {
	public static final String NS = DictionaryTypeBean.class.getName();

	@SuppressWarnings("unchecked")
	public List<DictionaryType> getDictionaryTypes() {
		return (List<DictionaryType>)sqlSession().selectList(NS + ".selectAllDictionaryTypes");
	}

	public DictionaryType getDictionaryTypesByCode(String code) {
		return (DictionaryType)sqlSession().selectOne(NS + ".selectDictionaryTypeByCode", code);
	}
}
