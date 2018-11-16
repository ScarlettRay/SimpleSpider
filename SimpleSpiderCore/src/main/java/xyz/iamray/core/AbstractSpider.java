package xyz.iamray.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.CrawlMes;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author liuwenrui
 * @since  2018/11/3
 *
 */
@Slf4j
@Data
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
    protected Properties property = null;


    /**
     *  store current spider`s information;
     */
    protected CrawlMes crawlMes = null;

    protected StartConfiger startConfiger = null;



    static{
        /*
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

    /**
     * 用户自定义线程池
     * @param cumstomizeExecutorService 自定义线程池
     * @param useCustomThreadPool 是否使用自定义的线程池
     * @return
     */
    public Spider customThreadPool(ExecutorService cumstomizeExecutorService,boolean useCustomThreadPool){
        this.setCumstomizeExecutorService(cumstomizeExecutorService);
        if(useCustomThreadPool){
            this.usingExecutorService = cumstomizeExecutorService;
        }
        return this;
    }

    @Override
    public <T1,T2> T2 serialCrawl(String url,CrawlerAction<T1,T2> crawlerAction){
        Future<T2> future = usingExecutorService.submit(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);
            //FIXME 不要让它抛警告
            Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass())[0];
            T1 re = HttpClientTool.get(url,
                    this.getHeader(),
                    this.startConfiger.getHttpClient(),
                    type);
            log.info("Crawling "+type.getName()+" success. Dealing result with your action");
            crawlMes.setCurrentUrl(url);
            return crawlerAction.crawl (re,this.crawlMes);
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
            T1 re = HttpClientTool.get(url,
                    this.getHeader(),
                    this.startConfiger.getHttpClient(),
                    type);
            log.info("Crawling "+type.getName()+" success. Dealing result with your action");
            this.crawlMes.setCurrentUrl(url);
            if(this.startConfiger.isCollection){
                this.startConfiger.getBlockingQueue().addAll((Collection)crawlerAction.crawl(re,this.crawlMes));
            }else{
                this.startConfiger.getBlockingQueue().add(crawlerAction.crawl(re,this.crawlMes));
            }
        });
    }


    public Spider setProperty(Properties property){
        this.property = property;
        return this;
    }

    public Spider setBlockingQueue(BlockingQueue blockingQueue){
        this.startConfiger.blockingQueue = blockingQueue;
        return this;
    }


    /**
     * 爬虫启动前必须配置此类
     */
    @Data
    protected class StartConfiger{
        private CrawlerAction crawlerAction;

        private CloseableHttpClient httpClient;

        private String url;

        private String[] urls;

        /**
         * async crawl need set blockingQueue
         */
        private BlockingQueue blockingQueue;

        private boolean isCollection;

        public void setCrawlerAction(CrawlerAction crawlerAction){
            this.crawlerAction = crawlerAction;
            this.isCollection = SpiderUtil.isArgumentsCollection(crawlerAction,1);
        }

        public void setHttpClient(CloseableHttpClient httpClient){
            if(httpClient == null){
                this.httpClient = HttpClientTool.getHttpClientWithConfig(getRetryTime(),getConnectTimeout());
            }else{
                this.httpClient = httpClient;
            }
        }

        public CrawlerAction getCrawlerAction(){
            return this.crawlerAction;
        }

    }

    public Spider setStarterConfiger(String[] urls,CrawlerAction crawlerAction,CloseableHttpClient httpClient){
        this.startConfiger = new StartConfiger();
        this.startConfiger.setUrls(urls);
        this.startConfiger.setCrawlerAction(crawlerAction);
        this.startConfiger.setHttpClient(httpClient);

        return this;
    }

    public Spider setStarterConfiger(String[] urls,CrawlerAction crawlerAction){
        return setStarterConfiger(urls, crawlerAction,null);
    }

    public Spider setStarterConfiger(String url,CrawlerAction crawlerAction,CloseableHttpClient httpClient){
        this.startConfiger = new StartConfiger();
        this.startConfiger.setUrl(url);
        this.startConfiger.setCrawlerAction(crawlerAction);
        this.startConfiger.setHttpClient(httpClient);

        return this;
    }

    public Spider setStarterConfiger(String url,CrawlerAction crawlerAction){
        return setStarterConfiger(url, crawlerAction,null);
    }
}
