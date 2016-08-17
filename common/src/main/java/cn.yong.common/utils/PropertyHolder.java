package cn.yong.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 用于获取配置文件的值的工具类
 */
public class PropertyHolder extends PropertyPlaceholderConfigurer {

    protected static final Logger logger = LogManager.getLogger(PropertyHolder.class);

    private static Map ctxPropertiesMap = new HashMap();

    @Override
    protected void processProperties(
            ConfigurableListableBeanFactory beanFactoryToProcess,
            Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }
    }

    static {
        readExternalFile("conf/device.properties");
    }

    public static void readExternalFile(String... paths) {
        try {
            for (String path : paths) {
                InputStream in = new FileInputStream(new File(path));
                Properties prop = new Properties();
                prop.load(in);
                for (Object obj : prop.keySet()) {
                    String key = String.valueOf(obj);
                    if (ctxPropertiesMap.containsKey(key)) {
                        logger.warn("Duplicate Conf :" + key);
                    } else {
                        ctxPropertiesMap.put(key, prop.getProperty(key));
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ReadExternalFile exception : ", ex);
        }

    }

    public static String getProperty(String name) {
        return String.valueOf(ctxPropertiesMap.get(name));
    }
}