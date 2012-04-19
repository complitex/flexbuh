package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:14
 */
@Stateless
@LocalBean
public class RegionBean extends AbstractBean implements ICrudBean<Region>{
    public static final String NS = RegionBean.class.getName();

    @Override
    public Long getId(Region region) {
        return sqlSession().selectOne(NS + ".selectRegionId", region);
    }

    @Override
    public void save(Region region) {
        if (region.getId() == null){
            sqlSession().insert(NS + ".insertRegion", region);
        }else {
            sqlSession().update(NS + ".updateRegion", region);
        }
    }

    @Override
    public Region load(Long id) {
        return getRegion(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteRegion", id);
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


