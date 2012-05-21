package org.complitex.flexbuh.common;

import org.complitex.flexbuh.common.service.ConfigBean;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Pavel Sknar
 *         Date: 08.02.12 17:12
 */
@Singleton(name="CommonModule")
@Startup
public class Module {
    @EJB
    private ConfigBean configBean;

//    @PostConstruct
//    public void init(){
//        configBean.init(ApplicationConfig.class.getCanonicalName(), ApplicationConfig.values());
//    }
}
