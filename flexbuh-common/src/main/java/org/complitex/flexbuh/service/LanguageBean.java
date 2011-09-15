package org.complitex.flexbuh.service;

import org.complitex.flexbuh.entity.Language;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 16.08.11 10:29
 */
@Stateless
public class LanguageBean extends AbstractBean {
	public static final String NS = LanguageBean.class.getName();

	@SuppressWarnings("unchecked")
	public List<Language> getLanguages() {
		return (List<Language>)sqlSession().selectList(NS + ".selectLanguages");
	}

	public Language getLanguage(Long id) {
		return (Language)sqlSession().selectOne(NS + ".selectLanguage", id);
	}

	public Language getLanguageByLangIsoCode(@NotNull String langIsoCode) {
		return (Language)sqlSession().selectOne(NS + ".selectLanguageByLangIsoCode", langIsoCode);
	}
}
