package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeFilter;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 15:46
 */
@Stateless
@LocalBean
public class FieldCodeBean extends AbstractBean implements ICrudBean<FieldCode>{
    public final static String NS = FieldCodeBean.class.getName();

    public FieldCode getFieldCode(Long id){
        return (FieldCode) sqlSession().selectOne(NS + ".selectFieldCode", id);
    }

    public List<FieldCode> getFieldCodes(FieldCodeFilter filter){
        return sqlSession().selectList(NS + ".selectFieldCodes", filter);
    }

    public Integer getFieldCodesCount(FieldCodeFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectFieldCodesCount", filter);
    }

    @Override
    public Long getId(FieldCode fieldCode) {
        //todo how to determine unique object?
        return null;
    }

    @Override
    public void insert(FieldCode fieldCode) {
        //todo add unique key
        sqlSession().insert(NS + ".insertFieldCode", fieldCode);

        for (Field field : fieldCode.getFields()) {
            field.setFieldCodeId(fieldCode.getId());
            sqlSession().insert(NS + ".insertField", field);
        }

        for (String code : fieldCode.getCodes()){
            Map<String, Object> map = new HashMap<>();
            map.put("fieldCodeId", fieldCode.getId());
            map.put("code", code);

            sqlSession().insert(NS + ".insertCode", map);
        }
    }

    @Override
    public void update(FieldCode fieldCode) {
        //todo
    }

    @Override
    public FieldCode load(Long id) {
        return getFieldCode(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteFieldCode", id);
    }

    public Field getField(String code, String name, String sprName){
        return (Field) sqlSession().selectOne(NS + ".selectField", new FieldCodeFilter(code, name, sprName));
    }

    public List<Field> getFields(String code){
        return sqlSession().selectList(NS + ".selectFields", code);
    }

    public void deleteAllFieldCode(){
        //todo on delete cascade
        sqlSession().delete(NS + ".deleteAllCode");
        sqlSession().delete(NS + ".deleteAllField");
        sqlSession().delete(NS + ".deleteAllFieldCode");
    }
}
