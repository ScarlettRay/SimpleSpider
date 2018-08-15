package xyz.iamray.api.impl;


import com.alibaba.fastjson.JSON;
import org.jsoup.nodes.Document;
import xyz.iamray.api.AbstractCrawlerAction;

/**
 * Created by liuwenrui on 2018/3/9
 */
public abstract class AbstractByteCrawlerAction<T> extends AbstractCrawlerAction<T> {
    @Override
    public T documentCrawl(Document document, String url) {
        return null;
    }

    @Override
    public T JSONCrawl(JSON jsonObject, String url) {
        return null;
    }
}
