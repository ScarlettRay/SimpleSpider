package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.CrawlMes;

import java.util.Collection;
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
public abstract class AbstractSpider extends SpiderProperty implements Spider{

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

    private CloseableHttpClient httpClient = null;


    /**
     *  store current spider`s information;
     */
    private CrawlMes crawlMes = null;


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
    public <T1, T2> T2 serialCrawl(String url,CrawlerAction<T1,T2> crawlerAction){
        Future<T2> future = usingExecutorService.submit(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);
            //FIXME
            Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass())[0];
            T1 re = HttpClientTool.get(url,this.getHeader(),getHttpClient(),type);
            log.info("Crawling "+type.getName()+" success. Dealing result with your action");
            return crawlerAction.crawl(re,this.crawlMes);
        });
        try {
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T1,T2> void asyncCrawl(String url,CrawlerAction<T1,T2> crawlerAction){
        usingExecutorService.execute(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);
            Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass())[0];
            T1 re = HttpClientTool.get(url,this.getHeader(),getHttpClient(),type);
            log.info("Crawling "+type.getName()+" success. Dealing result with your action");
            boolean isCollection = SpiderUtil.isArgumentsCollection(crawlerAction,1);
            if(isCollection){
                this.getBlockingQueue().addAll((Collection)crawlerAction.crawl(re,this.crawlMes));
            }else{
                this.getBlockingQueue().add(crawlerAction.crawl(re,this.crawlMes));
            }
        });
    }

    public Spider setHttpClient(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
        return this;
    }

    private CloseableHttpClient getHttpClient(){
        if(this.httpClient == null){
            return HttpClientTool.getHttpClientWithConfig(getRetryTime());
        }else{
            return this.httpClient;
        }
    }

}
