package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:14
 */
@Stateless
public class RegionBean extends AbstractBean {
    public static final String NS = RegionBean.class.getName();

    public void save(Region region) {
        sqlSession().insert(NS + ".insertRegion", region);
    }

	public void update(Region region) {
		sqlSession().update(NS + ".updateRegion", region);
	}

    public Region getRegion(Long id) {
        return (Region)sqlSession().selectOne(NS + ".selectRegion", id);
    }

	public List<Region> getRegionByCode(Integer code) {
		return sqlSession().selectList(NS + ".selectRegionByCode", code);
	}

    public List<Region> getRegions(FilterWrapper<Region> filter) {
        return sqlSession().selectList(NS + ".selectRegions", filter);
    }

    public Integer getRegionsCount(FilterWrapper<Region> filter){
        return (Integer) sqlSession().selectOne(NS + ".selectRegionsCount", filter);
    }
}


