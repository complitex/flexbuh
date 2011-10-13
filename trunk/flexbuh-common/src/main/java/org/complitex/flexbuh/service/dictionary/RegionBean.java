package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.Region;
import org.complitex.flexbuh.service.AbstractBean;

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
    public List<Region> getRegions() {
        return (List<Region>)sqlSession().selectList(NS + ".selectAllRegions");
    }

    public Integer getRegionsCount(){
        return (Integer) sqlSession().selectOne(NS + ".selectAllRegionsCount");
    }

    @SuppressWarnings("unchecked")
    public List<Region> getRegions(int first, int count) {
        return sqlSession().selectList(NS + ".selectRegions", new AbstractFilter(first, count));
    }
}


