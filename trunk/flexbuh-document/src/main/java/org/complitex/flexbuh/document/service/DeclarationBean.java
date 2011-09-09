package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.09.11 15:07
 */
@Stateless
public class DeclarationBean extends AbstractBean{
    public final static String NS = DeclarationBean.class.getName();

    public void save(Declaration declaration){
        sqlSession().insert(NS + ".insertDeclaration", declaration);

        for (DeclarationValue value : declaration.getValues()){
            value.setDeclarationId(declaration.getId());
            sqlSession().insert(NS + ".insertDeclarationValue", value);
        }
    }

    public Declaration getDeclaration(Long id){
        return (Declaration) sqlSession().selectOne(NS + ".selectDeclaration", id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Declaration> getDeclarations(DeclarationFilter filter){
        return sqlSession().selectList(NS + ".selectDeclarations", filter);
    }

    public Integer getDeclarationsCount(DeclarationFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectDeclarationsCount", filter);
    }
}
