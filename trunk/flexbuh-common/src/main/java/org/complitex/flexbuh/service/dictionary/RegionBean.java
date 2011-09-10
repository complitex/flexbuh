package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.Region;
import org.complitex.flexbuh.entity.dictionary.RegionName;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:14
 */
@Stateless
public class RegionBean extends DictionaryBean<Region> {
    public static final String NS = RegionBean.class.getName();

	@Override
    public void create(Region region) {
        sqlSession().insert(NS + ".create", region);
		Map<String, Object> params = Maps.newHashMap();
		params.put("regionId", region.getId());
		for (RegionName regionName : region.getNames()) {
			params.put("languageId", regionName.getLanguage().getId());
			params.put("val", regionName.getValue());
			sqlSession().insert(RegionName.class.getName() + ".create", params);
		}
    }

	@SuppressWarnings("unchecked")
	public List<Region> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<Region>)sqlSession().selectList(NS + ".readAll", params);
	}

	public Region read(long id) {
		return (Region)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}

	@SuppressWarnings("unchecked")
	public List<Region> read(int first, int count) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("first", first);
		params.put("count", count);
		return sqlSession().selectList(NS + ".readLimit", params);
	}

	@Override
	public String getTable() {
		return Region.TABLE;
	}
}
