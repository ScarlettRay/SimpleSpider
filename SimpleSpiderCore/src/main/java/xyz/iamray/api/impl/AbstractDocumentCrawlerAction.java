package xyz.iamray.api.impl;


import com.alibaba.fastjson.JSON;
import xyz.iamray.api.AbstractCrawlerAction;

/**
 * Created by liuwenrui on 2018/3/3
 */
public abstract class AbstractDocumentCrawlerAction<T> extends AbstractCrawlerAction<T> {


    @Override
    public T JSONCrawl(JSON jsonObject, String url) {
        return null;
    }

    @Override
    public T FileCrawl(byte[] bytes,String url) {
        return null;
    }
}
