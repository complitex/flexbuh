package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.entity.dictionary.CurrencyName;
import org.complitex.flexbuh.service.AbstractBean;

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

        for (CurrencyName currencyName : currency.getNames()) {
            currencyName.setCurrencyId(currency.getId());

            sqlSession().insert(NS + ".insertCurrencyName", currencyName);
        }
    }

    public Currency getCurrency(Long id) {
        return (Currency)sqlSession().selectOne(NS + ".selectCurrency", id);
    }

    @SuppressWarnings("unchecked")
    public List<Currency> getCurrencies() {
        return (List<Currency>)sqlSession().selectList(NS + ".selectAllCurrencies");
    }

    public Integer getCurrenciesCount(){
        return (Integer) sqlSession().selectOne(NS + ".selectAllCurrenciesCount");
    }

    @SuppressWarnings("unchecked")
    public List<Currency> getCurrencies(int first, int count) {
        return sqlSession().selectList(NS + ".selectCurrencies", new AbstractFilter(first, count));
    }
}
