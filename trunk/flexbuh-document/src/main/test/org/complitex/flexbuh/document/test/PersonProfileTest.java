package org.complitex.flexbuh.document.test;

import org.complitex.flexbuh.document.entity.PersonProfile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.12.11 18:03
 */
public class PersonProfileTest {
    public static void main(String... args){
        PersonProfile pp1 = new PersonProfile();
        pp1.setFirstName("FN");
        pp1.setLastName("LN");
        pp1.setMiddleName("MN");

        pp1.mergePhysicalNames();

        System.out.println(pp1);


        PersonProfile pp2 = new PersonProfile();
        pp2.setName("LN FN MN");

        pp2.parsePhysicalNames();

        System.out.println(pp2);
    }
}
