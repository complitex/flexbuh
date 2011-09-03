package org.complitex.flexbuh.service.user;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.user.PersonType;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 13:45
 */
@Stateless
public class PersonTypeBean extends AbstractBean {
	public static final String NS = PersonTypeBean.class.getName();

	@SuppressWarnings("unchecked")
	public List<PersonType> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", PersonType.TABLE);
		return (List<PersonType>)sqlSession().selectList(NS + ".readAll", params);
	}

	public PersonType findByCode(String code) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", PersonType.TABLE);
		params.put("code", code);
		return (PersonType)sqlSession().selectOne(NS + ".findByCode", params);
	}
}
