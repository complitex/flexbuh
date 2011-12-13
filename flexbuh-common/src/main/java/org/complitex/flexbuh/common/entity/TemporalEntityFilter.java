package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.util.StringUtil;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 18:05
 */
public class  TemporalEntityFilter<T extends AbstractTemporalEntity> {
    private Class<T> _class;

    public TemporalEntityFilter() {
    }

    public TemporalEntityFilter(Class<T> _class) {
       this._class = _class;
    }

    public String getTableName() {
        return StringUtil.underline(_class.getSimpleName());
    }
    
    public String getClassName(){
        return _class.getName();
    }
}
