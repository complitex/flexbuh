package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.util.StringUtil;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 18:05
 */
public class  TemporalEntityFilter<T extends AbstractTemporalEntity> {
    private Long sessionId;
    private Class<T> _class;

    public TemporalEntityFilter() {
    }

    public TemporalEntityFilter(Long sessionId, Class<T> _class) {
        this.sessionId = sessionId;
        this._class = _class;
    }

    public String getTableName() {
        return StringUtil.underline(_class.getSimpleName());
    }
    
    public String getClassName(){
        return _class.getName();
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Class<T> get_class() {
        return _class;
    }

    public void set_class(Class<T> _class) {
        this._class = _class;
    }
}
