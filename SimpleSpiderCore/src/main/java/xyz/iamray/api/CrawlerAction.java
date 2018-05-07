package xyz.iamray.api;


import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;

import java.util.Properties;

public interface CrawlerAction<T> {

    String DocumentCrawl = "documentCrawl";

    String JSONCrawl = "JSONCrawl";

    /**
     * html文件爬取
     * @return
     */
     T documentCrawl(Document document, String url);

    /**
     * restAPI数据
     * @param jsonObject
     * @return
     */
     T JSONCrawl(JSONObject jsonObject, String url);

    /**
     * 文件爬取
     * @param bytes
     * @return
     */
     T FileCrawl(byte[] bytes, String url);

    <T>T getAttr(String key, Class<T> clazz);

    void setProperty(Properties property);
}
