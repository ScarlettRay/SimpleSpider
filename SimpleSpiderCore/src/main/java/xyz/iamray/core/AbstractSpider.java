package xyz.iamray.core;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.action.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.link.http.HttpClientTool;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
@Slf4j
public class AbstractSpider extends SpiderProperty implements Spider{

    /**
     * 默认线程池
     */
    private static ExecutorService defaultExecutorService;

    /**
     * 正在使用的线程池
     */
    private ExecutorService usingExecutorService;

    /**
     * 用户属性，用于与外部进行交互的属性储存
     */
    private Properties property = null;


    static{
        /**
         * 创建线程池，单例模式
         */
        defaultExecutorService = Executors.newFixedThreadPool(5);
    }

    /**
     * 使用默认线程池
     * @return
     */
    public Spider defaultThreadPool(){
        usingExecutorService = defaultExecutorService;
        return this;
    }

    @Override
    public <T1, T2> T2 serialCrawl(String url,CrawlerAction<T1,T2> crawlerAction) {
        Future<T2> future = usingExecutorService.submit(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);

            if(crawlerAction instanceof AbstractDocumentCrawlerAction){
                log.debug("Crawling document: "+url);
                Document document = HttpClientTool.getDocumentWithHttpClient(
                        url,
                        this.getHeader(),
                        this.);

                return  crawlerAction.documentCrawl(document,url);
            }else if(crawlerAction instanceof AbstractJsonCrawlerAction){
                logger.debug("Crawling json: "+url);
                JSON json = HttpClientTool.getJSONWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);

                return crawlerAction.JSONCrawl(json,url);
            }else{
                logger.debug("Crawling file/image:"+url);
                byte[] bytes = HttpClientTool.getBytesWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);
                return crawlerAction.FileCrawl(bytes,url);
            }
        });
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void asyncCrawl(String url,CrawlerAction crawlerAction) {

    }
}
