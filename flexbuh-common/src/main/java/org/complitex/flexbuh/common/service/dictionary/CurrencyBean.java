package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Currency;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 11.08.11 14:10
 */
@Stateless
@LocalBean
public class CurrencyBean extends AbstractBean implements ICrudBean<Currency> {
    public static final String NS = CurrencyBean.class.getName();

    @Override
    public Long getId(Currency currency){
        return sqlSession().selectOne(NS + ".selectCurrencyId", currency);
    }

    public void save(Currency currency){
        if (currency.getId() == null){
            sqlSession().insert(NS + ".insertCurrency", currency);
        }else {
            sqlSession().update(NS + ".updateCurrency", currency);
        }
    }

    @Override
    public Currency load(Long id) {
        return getCurrency(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteCurrency", id);
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
