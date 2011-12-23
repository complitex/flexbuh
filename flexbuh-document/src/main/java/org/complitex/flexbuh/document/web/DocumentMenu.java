package org.complitex.flexbuh.document.web;

import org.complitex.flexbuh.common.template.ResourceTemplateMenu;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.08.11 15:08
 */
public class DocumentMenu extends ResourceTemplateMenu{
    public DocumentMenu() {
        add("person_profile_list", PersonProfileList.class);
        add("declaration_list", DeclarationList.class);
        add("counterpart_list", CounterpartList.class);
        add("employee_list", EmployeeList.class);
    }
}
