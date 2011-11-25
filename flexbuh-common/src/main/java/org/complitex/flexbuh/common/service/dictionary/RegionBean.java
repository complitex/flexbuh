package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.entity.dictionary.RegionFilter;
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

	@SuppressWarnings("unchecked")
	public List<Region> getRegionByCode(Integer code) {
		return sqlSession().selectList(NS + ".selectRegionByCode", code);
	}

    @SuppressWarnings("unchecked")
    public List<Region> getRegions(RegionFilter filter) {
        return (List<Region>)sqlSession().selectList(NS + ".selectRegions", filter);
    }

    public Integer getRegionsCount(RegionFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectRegionsCount", filter);
    }
}


