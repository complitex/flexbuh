package org.complitex.flexbuh.common.util;

import org.complitex.flexbuh.common.entity.ILongId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 17:45
 */
public class ListUtil {
    /**
     * @param list1 List
     * @param list2 List
     * @param <T> ILongId
     * @return Элементы из list1, которые не содержатся в list2 (list1 - list2)
     */
    public static <T extends ILongId> List<T> getDiffById(List<T> list1, List<T> list2){
        List<T> list = new ArrayList<T>();

        for (T o1 : list1){
            boolean found = false;

            for (T o2 : list2){
                if (o1.getId() != null && o1.getId().equals(o2.getId())){
                    found = true;

                    break;
                }
            }

            if (!found){
                list.add(o1);
            }
        }

        return list;
    }
}
