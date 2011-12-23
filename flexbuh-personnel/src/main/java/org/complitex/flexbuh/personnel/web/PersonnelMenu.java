package org.complitex.flexbuh.personnel.web;

import org.complitex.flexbuh.common.template.ResourceTemplateMenu;
import org.complitex.flexbuh.common.web.TemporalEntityListPage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 17:02
 */
public class PersonnelMenu extends ResourceTemplateMenu{
    public PersonnelMenu() {
        add("department", TemporalEntityListPage.class);
    }
}
