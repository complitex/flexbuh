package org.compliex.flexbux.common.test;

import org.complitex.flexbuh.common.util.StringUtil;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.12.11 16:11
 */
public class UnderlineTest {
    public static void main(String... args){
        testUnderline();
    }
    
    private static void testUnderline(){
        System.out.println(StringUtil.underline(UnderlineTest.class.getSimpleName()));
    }
}
