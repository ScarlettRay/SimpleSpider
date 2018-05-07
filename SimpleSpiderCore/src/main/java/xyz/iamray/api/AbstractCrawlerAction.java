package xyz.iamray.api;

import java.util.Properties;

/**
 * Created by liuwenrui on 2018/4/12
 */
public abstract class AbstractCrawlerAction<T> implements CrawlerAction<T> {

    private Properties property = null;

    @Override
    public <T> T getAttr(String key, Class<T> clazz) {
        if(property == null){
            throw new NullPointerException();
        }
        return (T)property.get(key);
    }

    public void setProperty(Properties property){
        this.property = property;
    }
}
