package com.oobss.ddns.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * TODO 请描述类的作用
 *
 * @author zhaomj
 * @date 2021/2/8
 */
public class PropertiesFileUtils {
    public static Properties getPropertiesFromUserDir(String configFileName) {
        Properties properties = new Properties();
        String rootPath = System.getProperty("user.dir");
        FileInputStream in = null;
        try {
            in = new FileInputStream(rootPath + File.separator + configFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
