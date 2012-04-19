package org.complitex.flexbuh.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.04.12 16:01
 */
public class FileUtil {
    private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static FileInputStream getFileInputStream(String dir, String subDir, String name){
        try {
            return new FileInputStream(dir + File.separator + subDir + File.separator + name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getFileNames(String dir, String subDir, final Pattern pattern){
        return  new File(dir, subDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        });
    }
}
