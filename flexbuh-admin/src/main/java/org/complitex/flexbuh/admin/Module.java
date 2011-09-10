package org.complitex.flexbuh.admin;

import org.complitex.flexbuh.admin.importexport.entity.DictionaryConfig;
import org.complitex.flexbuh.service.ConfigBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Pavel Sknar
 *         Date: 10.09.11 23:33
 */
@Singleton(name="AdminModule")
@Startup
public class Module {
    public final static String NAME = "org.complitex.flexbuh.admin";

    @EJB
    private ConfigBean configBean;

    @PostConstruct
    public void init(){
        configBean.init(DictionaryConfig.class.getCanonicalName(), DictionaryConfig.values());
    }
}
