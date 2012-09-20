package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Department;
import org.complitex.flexbuh.personnel.entity.Position;
import org.complitex.flexbuh.personnel.entity.PositionFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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
            position.getDepartmentAttributes().setVersion(2L);
            sqlSession().insert(NS + ".insertDepartmentAttributePosition", position);
        }
    }

    @Transactional
    public void update(@NotNull Position position) {
        /*
        if (position.getDepartmentAttributes().isNew()) {
            sqlSession().insert(NS + ".insertDepartmentAttributePosition", position);
            return;
        }*/
        if (position.getDepartment() != null && position.getDepartmentAttributes() != null) {
            if (position.getDepartmentAttributes().isNotNew()) {
                sqlSession().update(NS + ".updateDepartmentAttributePositionNullCompletionDate", position);
            }
            sqlSession().insert(NS + ".updateDepartmentAttributePosition", position);
            return;
        }
        sqlSession().update(NS + ".updatePositionNullCompletionDate", position);
        sqlSession().insert(NS + ".updatePosition", position);
    }

    @Transactional
    public void updateCompletionDate(@NotNull Position position) {
        if (position.getDepartment() != null && position.getDepartmentAttributes() != null) {
            sqlSession().update(NS + ".updatePositionCompletionDate", position);
            return;
        }
        sqlSession().update(NS + ".updateDepartmentAttributePositionCompletionDate", position);
    }

    @Override
    @NotNull
    public <A extends TemporalDomainObjectFilter> List<Position> getTDOObjects(@Null A f) {
        PositionFilter filter = (PositionFilter)f;
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        if (filter.getCurrentDate() != null && filter.getCurrentDate().getTime() == Long.MAX_VALUE) {
            return Lists.newArrayList(getTDObjectLastInHistory(filter.getId(), filter.getDepartmentId()));
        } else if (filter.getCurrentDate() != null && filter.getCurrentDate().getTime() == 0 && filter.getDepartmentId() != null) {
            return Lists.newArrayList(getPositionByDepartment(filter.getId(), filter.getDepartmentId(), filter.getEntryIntoForceDate()));
        } else if (filter.getCurrentDate() != null && filter.getCurrentDate().getTime() == 0 ) {
            return Lists.newArrayList(getTDObject(filter.getId(), 1));
        } else if (filter.getDepartmentId() != null) {
            List<Position> sqlResult = sqlSession().selectList(NS + ".selectCurrentDepartmentPositions", filter);
            List<Position> positions = Lists.newArrayList();
            Position prevPosition = null;
            for (Position position : sqlResult) {
                if (position.getDepartment() != null && prevPosition != null && position.getId().equals(prevPosition.getId())) {
                    prevPosition.setDepartment(position.getDepartment());
                    if (filter.getCurrentDate() != null &&
                            position.getEntryIntoForceDate().after(filter.getCurrentDate()) &&
                            (prevPosition.getCompletionDate() == null || prevPosition.getCompletionDate().after(position.getEntryIntoForceDate()))) {
                        prevPosition.setCompletionDate(position.getEntryIntoForceDate());
                    } else {
                        prevPosition.copyDepartmentAttributes(position);
                    }
                    prevPosition = null;
                } else if (prevPosition != null && prevPosition.getDepartment() != null && position.getId().equals(prevPosition.getId())) {
                    position.setDepartment(prevPosition.getDepartment());
                    if (filter.getCurrentDate() != null &&
                            prevPosition.getEntryIntoForceDate().after(filter.getCurrentDate()) &&
                            (position.getCompletionDate() == null || position.getCompletionDate().after(prevPosition.getEntryIntoForceDate()))) {
                        position.setCompletionDate(position.getEntryIntoForceDate());
                    } else {
                        position.copyDepartmentAttributes(prevPosition);
                    }
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
        }
        return sqlSession().selectList(NS + ".selectCurrentOrganizationPositions", filter);
    }

    public int getPositionsCount(@Null PositionFilter filter) {
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }
        SqlSession sqlSession = sqlSession();
        log.debug("sql session: {}", sqlSession);

        return (Integer)sqlSession.selectOne(NS + ".selectCurrentPositionsCount", filter);
    }

    public Position getTDObject(@NotNull Long id, @Null Department department) {
        return getTDObject(id, department, new Date());
    }

    public Position getTDObject(@NotNull Long id, @Null Department department, @NotNull Date currentDate) {
        if (department == null || department.getId() == null) {
            return getTDObject(id);
        }
        return getPositionByDepartment(id, department.getId(), currentDate);
    }

    public Position getPositionByDepartment(@NotNull Long id, @Null Long departmentId, @NotNull Date currentDate) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("currentDate", currentDate);
        params.put("departmentId", departmentId);

        List<Position> result = sqlSession().selectList(NS + ".selectCurrentTDObjectById", params);

        log.debug("PositionBean.selectCurrentTDObjectById result: {} ({})", result, result.size());

        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        result.get(0).setDepartment(result.get(1).getDepartment());
        if (currentDate.after(result.get(1).getEntryIntoForceDate())) {
            result.get(0).copyDepartmentAttributes(result.get(1));
        } else {
            result.get(0).setCompletionDate(result.get(1).getEntryIntoForceDate());
        }
        return result.get(0);
    }

    public Position getTDObjectLastInHistory(Long id, Long departmentId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("departmentId", departmentId);
        List<Position> result = sqlSession().selectList(NS + ".selectTDObjectLastInHistory", params);

        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        result.get(0).copyDepartmentAttributes(result.get(1));
        return result.get(0);
    }

    public Position getTDObjectPreviewInHistoryByField(Long id, Long version, Long departmentId, String fieldName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);
        params.put("fieldName", fieldName);
        params.put("departmentId", departmentId);

        List<Position> result = sqlSession().selectList(NS + ".selectDepartmentPositionPreviewInHistoryByField", params);

        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        result.get(0).setDepartment(result.get(1).getDepartment());
        result.get(0).copyDepartmentAttributes(result.get(1));
        /*
        } else {
            result.get(0).setCompletionDate(result.get(1).getEntryIntoForceDate());
        }*/
        return result.get(0);
    }

    public Position getTDObjectNextInHistoryByField(Long id, Long version, Long departmentId, String fieldName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("version", version);
        params.put("fieldName", fieldName);
        params.put("departmentId", departmentId);

        List<Position> result = sqlSession().selectList(NS + ".selectDepartmentPositionNextInHistoryByField", params);

        if (result.size() == 0) {
            return null;
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        result.get(0).setDepartment(result.get(1).getDepartment());
        result.get(0).copyDepartmentAttributes(result.get(1));
        /*
        } else {
            result.get(0).setCompletionDate(result.get(1).getEntryIntoForceDate());
        }*/
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
