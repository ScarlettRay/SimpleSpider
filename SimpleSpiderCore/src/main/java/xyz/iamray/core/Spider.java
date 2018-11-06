package xyz.iamray.core;

import xyz.iamray.action.CrawlerAction;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public interface Spider {


    /**
     * <p>Serial crawl
     * @param crawlerAction
     * @param <T1>
     * @param <T2>
     * @return
     */
    <T1,T2> T2 serialCrawl(String url,CrawlerAction<T1,T2> crawlerAction);


    /**
     * Asynchronous crawl
     * @param crawlerAction
     */
    void asyncCrawl(String url,CrawlerAction crawlerAction);

}
