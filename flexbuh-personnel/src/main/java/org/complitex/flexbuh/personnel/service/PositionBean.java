package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Position;
import org.complitex.flexbuh.personnel.entity.PositionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 18.07.12 11:57
 */
@Stateless
public class PositionBean extends TemporalDomainObjectBean<Position> {

    private static final Logger log = LoggerFactory.getLogger(PositionBean.class);

    public static final String NS = PositionBean.class.getName();

    public PositionBean() {
        super(NS);
    }

    @Transactional
    @Override
    public void save(@NotNull Position position) {
        if (position.getId() != null) {
            update(position);
        } else {
            create(position);
        }
    }

    @Transactional
    public void create(@NotNull Position position) {
        position.setVersion(1L);
        sqlSession().insert(NS + ".insertPosition", position);
        if (position.getDepartment() != null && position.getDepartmentAttributes() != null) {
            sqlSession().insert(NS + ".insertDepartmentAttributePosition", position);
        }
    }

    @Transactional
    public void update(@NotNull Position position) {
        sqlSession().update(NS + ".updatePositionNullCompletionDate", position);
        sqlSession().insert(NS + ".updatePosition", position);
    }

    @Transactional
    public void updateCompletionDate(@NotNull Position position) {
        sqlSession().update(NS + ".updatePositionCompletionDate", position);
    }

    public List<Position> getPositions(@Nullable PositionFilter filter) {
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        if (filter.getDepartmentId() != null && filter.getOrganizationId() != null) {
            List<Position> sqlResult = sqlSession().selectList(NS + ".selectCurrentDepartmentPositions", filter);
            List<Position> positions = Lists.newArrayList();
            Position prevPosition = null;
            for (Position position : sqlResult) {
                if (position.getDepartment() != null && prevPosition != null && position.getId().equals(prevPosition.getId())) {
                    prevPosition.setDepartment(position.getDepartment());
                    prevPosition.copyDepartmentAttributes(position);
                    prevPosition = null;
                } else if (prevPosition != null && prevPosition.getDepartment() != null && position.getId().equals(prevPosition.getId())) {
                    position.setDepartment(position.getDepartment());
                    position.copyDepartmentAttributes(prevPosition);
                    positions.add(position);
                    prevPosition = null;
                } else if(position.getDepartment() == null) {
                    prevPosition = position;
                    positions.add(position);
                } else {
                    prevPosition = position;
                }
            }
            return positions;
        } else if (filter.getOrganizationId() != null) {
            return sqlSession().selectList(NS + ".selectCurrentOrganizationPositions", filter);
        }
        return Lists.newArrayList();
    }

    public int getPositionsCount(@Nullable PositionFilter filter) {
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }
        SqlSession sqlSession = sqlSession();
        log.debug("sql session: {}", sqlSession);

        return (Integer)sqlSession.selectOne(NS + ".selectCurrentPositionsCount", filter);
    }

    public Position getTDObject(@NotNull Long id, @Nullable Department department) {
        if (department == null || department.getId() == null) {
            return getTDObject(id);
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", new Date());
        params.put("departmentId", department.getId());

        List<Position> result = sqlSession().selectList(NS + ".selectCurrentTDObjectById", params);

        if (result.size() == 0) {
            return null;
        }
        result.get(0).setDepartment(department);
        if (result.size() == 1) {
            return result.get(0);
        }
        result.get(0).copyDepartmentAttributes(result.get(1));
        return result.get(0);
    }

    @Transactional
    public void deletePosition(Position position) {
        position.setDeleted(true);
        if (position.getCompletionDate() == null) {
            position.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".delete", position);
    }

}
