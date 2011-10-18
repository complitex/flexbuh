package org.complitex.flexbuh.document.entity;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 17.10.11 15:07
*/
public enum PersonType {
    JURIDICAL_PERSON(1), PHYSICAL_PERSON(2), JOINT_AGREEMENT(3), REPRESENTATIVE(4), PROPERTY_AGREEMENT(5);

    private int code;

    private PersonType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PersonType get(int code){
        for (PersonType personType : PersonType.values()){
            if (personType.getCode() == code){
                return personType;
            }
        }

        return null;
    }
}
