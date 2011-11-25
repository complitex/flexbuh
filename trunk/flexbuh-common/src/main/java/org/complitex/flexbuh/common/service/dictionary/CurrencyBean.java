package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Currency;
import org.complitex.flexbuh.common.entity.dictionary.CurrencyFilter;
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

	@SuppressWarnings("unchecked")
    public List<Currency> getCurrencyByCodeNumber(Integer codeNumber) {
        return sqlSession().selectList(NS + ".selectCurrenciesByCodeNumber", codeNumber);
    }

    @SuppressWarnings("unchecked")
    public List<Currency> getCurrencies(CurrencyFilter filter) {
        return (List<Currency>)sqlSession().selectList(NS + ".selectCurrencies", filter);
    }

    public Integer getCurrenciesCount(CurrencyFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectCurrenciesCount", filter);
    }
}
