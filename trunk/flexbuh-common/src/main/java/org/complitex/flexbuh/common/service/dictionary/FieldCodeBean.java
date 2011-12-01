package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeFilter;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 15:46
 */
@Stateless
public class FieldCodeBean extends AbstractBean{
    public FieldCode getFieldCode(Long id){
        return (FieldCode) sqlSession().selectOne("selectFieldCode", id);
    }

    @SuppressWarnings("unchecked")
    public List<FieldCode> getFieldCodes(FieldCodeFilter filter){
        return sqlSession().selectList("selectFieldCodes", filter);
    }

    public Integer getFieldCodesCount(FieldCodeFilter filter){
        return (Integer) sqlSession().selectOne("selectFieldCodesCount", filter);
    }

    public void save(FieldCode fieldCode){
        //todo add unique key
        if (fieldCode.getId() == null) {
            sqlSession().insert("insertFieldCode", fieldCode);

            for (Field field : fieldCode.getFields()) {
                field.setFieldCodeId(fieldCode.getId());
                sqlSession().insert("insertField", field);
            }

            for (String code : fieldCode.getCodes()){
                Map<String, Object> map = new HashMap<>();
                map.put("fieldCodeId", fieldCode.getId());
                map.put("code", code);

                sqlSession().insert("insertCode", map);
            }
        }else{
            //todo
        }
    }

    public Field getField(String code, String name, String sprName){
        return (Field) sqlSession().selectOne("selectField", new FieldCodeFilter(code, name, sprName));
    }

    @SuppressWarnings("unchecked")
    public List<Field> getFields(String code){
        return sqlSession().selectList("selectFields", code);
    }
}
