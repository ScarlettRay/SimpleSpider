package xyz.iamray.action;

import java.util.Properties;

/**
 * Created by liuwenrui on 2018/4/12
 */
public abstract class AbstractCrawlerAction<T1,T2> implements CrawlerAction<T1,T2> {

    private Properties property = null;

    @Override
    public <T> T getAttr(String key, Class<T> clazz) {
        if(property == null){
            return null;
        }
        return (T)property.get(key);
    }

    public void setProperty(Properties property){
        this.property = property;
    }}
