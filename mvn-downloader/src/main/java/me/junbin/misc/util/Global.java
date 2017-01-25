package me.junbin.misc.util;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/23 22:36
 * @description :
 */
public enum Global {

    ;

    private static final Properties PROPERTIES = new Properties();

    static {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:bundle/app.properties");
        try {
            PROPERTIES.load(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String normalize(String filePath) {
        if (filePath == null) {
            return null;
        }
        return Paths.get(filePath).normalize().toString();
    }

    public static String slashPath(String filePath) {
        if (filePath == null) {
            return null;
        }
        return Paths.get(filePath).normalize().toString().replaceAll("\\\\+", "/");
    }

    public static String imageUrl() {
        return PROPERTIES.getProperty("image.mapping.url");
    }

    public static String imageLocation() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return PROPERTIES.getProperty("windows.image.mapping.location");
        } else /*if (SystemUtils.IS_OS_LINUX)*/ {
            return PROPERTIES.getProperty("linux.image.mapping.location");
        }
    }

    public static String docUrl() {
        return PROPERTIES.getProperty("doc.mapping.url");
    }

    public static String docLocation() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return PROPERTIES.getProperty("windows.doc.mapping.location");
        } else {
            return PROPERTIES.getProperty("linux.doc.mapping.location");
        }
    }

}
