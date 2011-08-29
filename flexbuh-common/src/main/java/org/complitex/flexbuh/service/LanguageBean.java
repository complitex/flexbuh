package org.complitex.flexbuh.service;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.Stub;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 16.08.11 10:29
 */
@Stateless
public class LanguageBean extends AbstractBean {
	public static final String NS = LanguageBean.class.getName();

	@SuppressWarnings("unchecked")
	public List<Language> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", Language.TABLE);
		return (List<Language>)sqlSession().selectList(NS + ".readAll", params);
	}

	public Language read(long id) {
		return (Language)sqlSession().selectOne(NS + ".findById", new Stub(id, Language.TABLE));
	}

	public Language find(@NotNull String langIsoCode) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", Language.TABLE);
		params.put("langIsoCode", langIsoCode);
		return (Language)sqlSession().selectOne(NS + ".findByLangIsoCode", params);
	}
}
