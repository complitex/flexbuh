package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Currency;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 11.08.11 14:10
 */
@Stateless
public class CurrencyBean extends AbstractBean {
    public static final String NS = CurrencyBean.class.getName();

    public void save(Currency currency) {
        sqlSession().insert(NS + ".insertCurrency", currency);
    }

	public void update(Currency currency) {
		sqlSession().update(NS + ".updateCurrency", currency);
	}

    public Currency getCurrency(Long id) {
        return (Currency)sqlSession().selectOne(NS + ".selectCurrency", id);
    }

    public List<Currency> getCurrencyByCodeNumber(Integer codeNumber) {
        return sqlSession().selectList(NS + ".selectCurrenciesByCodeNumber", codeNumber);
    }

    public List<Currency> getCurrencies(FilterWrapper<Currency> filter) {
        return sqlSession().selectList(NS + ".selectCurrencies", filter);
    }

    public Integer getCurrenciesCount(FilterWrapper<Currency> filter){
        return (Integer) sqlSession().selectOne(NS + ".selectCurrenciesCount", filter);
    }
}
