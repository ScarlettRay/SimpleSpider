package xyz.iamray.action;

import java.util.Map;
import java.util.Properties;

/**
 * Created by liuwenrui on 2018/4/12
 *
 */
public abstract class AbstractCrawlerAction<T1,T2> implements CrawlerAction<T1,T2> {

    private Properties property = null;
    private Map<String,Object> properties = null;

    @Override
    public <T> T getAttr(String key, Class<T> clazz) {
        if(property == null){
            return null;
        }
        return (T)property.get(key);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz){
        if(properties == null){
            return null;
        }
        return (T)properties .get(key);
    }

    public void setProperty(Properties property){
        this.property = property;
    }

    public void setProperty(Map<String,Object> properties){
        this.properties = properties;
    }


}
