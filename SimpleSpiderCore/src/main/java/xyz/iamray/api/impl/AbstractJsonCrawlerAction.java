package xyz.iamray.api.impl;


import org.jsoup.nodes.Document;
import xyz.iamray.api.AbstractCrawlerAction;

/**
 * Created by liuwenrui on 2018/3/9
 */
public abstract class AbstractJsonCrawlerAction<T> extends AbstractCrawlerAction<T> {
    @Override
    public  T documentCrawl(Document document, String url) {
        return null;
    }


    @Override
    public  T FileCrawl(byte[] bytes,String url) {
        return null;
    }
}
