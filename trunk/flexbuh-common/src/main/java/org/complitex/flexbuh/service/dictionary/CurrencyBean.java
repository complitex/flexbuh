package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.entity.dictionary.CurrencyName;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 11.08.11 14:10
 */
@Stateless
public class CurrencyBean extends DictionaryBean<Currency> {
    public static final String NS = CurrencyBean.class.getName();

	@Override
    public void create(Currency currency) {
        sqlSession().insert(NS + ".create", currency);
		Map<String, Object> params = Maps.newHashMap();
		params.put("currencyId", currency.getId());
		for (CurrencyName currencyName : currency.getNames()) {
			params.put("languageId", currencyName.getLanguage().getId());
			params.put("val", currencyName.getValue());
			sqlSession().insert(CurrencyName.class.getName() + ".create", params);
		}
    }

	@SuppressWarnings("unchecked")
	public List<Currency> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<Currency>)sqlSession().selectList(NS + ".readAll", params);
	}

	public Currency read(long id) {
		return (Currency)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}


	@Override
	public String getTable() {
		return Currency.TABLE;
	}
}
