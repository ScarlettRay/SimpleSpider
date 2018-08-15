package xyz.iamray.api;


import com.alibaba.fastjson.JSON;
import org.jsoup.nodes.Document;

import java.util.Properties;

public interface CrawlerAction<T> {

    /**
     * html文件爬取
     * @return
     */
    T documentCrawl(Document document, String url);

    /**
     * restAPI数据
     * @param json
     * @return
     */
    T JSONCrawl(JSON json, String url);

    /**
     * 文件爬取
     * @param bytes
     * @return
     */
    T FileCrawl(byte[] bytes, String url);

    <T>T getAttr(String key, Class<T> clazz);

    void setProperty(Properties property);
}
