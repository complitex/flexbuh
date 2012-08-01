package org.complitex.flexbuh.personnel.service;

import org.apache.ibatis.session.SqlSession;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Organization;
import org.complitex.flexbuh.personnel.entity.Position;
import org.complitex.flexbuh.personnel.entity.PositionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

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
    public void save(Position position) {
        if (position.getId() != null) {
            update(position);
        } else {
            create(position);
        }
    }

    @Transactional
    public void create(Position position) {
        position.setVersion(1L);
        sqlSession().insert(NS + ".insertPosition", position);
        if (position.getDepartment() != null && position.getDepartmentAttributes() != null) {
            sqlSession().insert(NS + ".insertDepartmentAttributePosition", position);
        }
    }

    @Transactional
    public void update(Position position) {
        sqlSession().update(NS + ".updatePositionNullCompletionDate", position);
        sqlSession().insert(NS + ".updatePosition", position);
    }

    @Transactional
    public void updateCompletionDate(Position position) {
        sqlSession().update(NS + ".updatePositionCompletionDate", position);
    }

    public List<Position> getPositions(PositionFilter filter) {
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return sqlSession().selectList(NS + ".selectCurrentPositions", filter);
    }

    public int getPositionsCount(PositionFilter filter) {
        if (filter == null) {
            filter = new PositionFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }
        SqlSession sqlSession = sqlSession();
        log.debug("sql session: {}", sqlSession);

        return (Integer)sqlSession.selectOne(NS + ".selectCurrentPositionsCount", filter);
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
