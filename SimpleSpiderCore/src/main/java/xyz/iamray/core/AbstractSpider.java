package xyz.iamray.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.CrawlMes;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuwenrui
 * @since  2018/11/3
 *
 */
@Slf4j
public abstract class AbstractSpider extends SpiderProperty implements Spider{

    /**
     * 默认线程池
     */
    private static ExecutorService defaultExecutorService;

    /**
     * 正在使用的线程池
     * 不设置则使用默认线程池
     */
    protected ExecutorService usingExecutorService = defaultExecutorService;

    /**
     * 用户属性，用于与外部进行交互的属性储存
     */
    @Deprecated
    protected Properties property = null;
    protected HashMap<Object,Object> pro = new HashMap<>();


    /**
     *  store current spider`s information;
     */
    protected CrawlMes crawlMes = null;

    protected StartConfiger startConfiger = new StartConfiger();


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
    public AbstractSpider defaultThreadPool(){
        usingExecutorService = defaultExecutorService;
        return this;
    }

    /**
     * 用户自定义线程池
     * @param cumstomizeExecutorService 自定义线程池
     * @param useCustomThreadPool 是否使用自定义的线程池
     * @return
     */
    public AbstractSpider customThreadPool(ExecutorService cumstomizeExecutorService,boolean useCustomThreadPool){
        this.setCumstomizeExecutorService(cumstomizeExecutorService);
        if(useCustomThreadPool){
            this.usingExecutorService = cumstomizeExecutorService;
        }
        return this;
    }


    @Deprecated
    public AbstractSpider setProperty(Properties property){
        this.property = property;
        return this;
    }

    public AbstractSpider setProperty(Object key,Object value){
        this.pro.put(key, value);
        return this;
    }

    public AbstractSpider setRequestHeader(Map<String,String> header){
        super.setHeader(header);
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

        private Map<String, String> postBody;

        protected BasicCookieStore cookieStore;

        /**
         * async crawl need set blockingQueue
         */
        private BlockingQueue blockingQueue;

        private boolean isCollection;

        private int listenHttpStatus = HttpStatus.SC_OK;//监听的http相应状态 默认200

        public void setCrawlerAction(CrawlerAction crawlerAction){
            this.crawlerAction = crawlerAction;
            this.isCollection = SpiderUtil.isArgumentsCollectionInSuperClass(crawlerAction,0);
        }

        public void setHttpClient(CloseableHttpClient httpClient){
            if(httpClient == null){
                this.httpClient = HttpClientTool.getHttpClientWithConfig(getRetryTime(),getConnectTimeout(),cookieStore);
            }else{
                this.httpClient = httpClient;
            }
        }


    }

    public AbstractSpider setStarterConfiger(String[] urls,Map<String, String> postBody,CrawlerAction crawlerAction,CloseableHttpClient httpClient){
        this.startConfiger.setUrls(urls);
        this.startConfiger.setCrawlerAction(crawlerAction);
        this.startConfiger.setHttpClient(httpClient);
        this.startConfiger.setPostBody(postBody);

        return this;
    }

    public AbstractSpider setStarterConfiger(String url,Map<String, String> postBody,CrawlerAction crawlerAction,CloseableHttpClient httpClient){
        this.startConfiger.setUrl(url);
        this.startConfiger.setCrawlerAction(crawlerAction);
        this.startConfiger.setHttpClient(httpClient);
        this.startConfiger.setPostBody(postBody);

        return this;
    }

    /**
     * get请求
     * @param urls
     * @param crawlerAction
     * @return
     */
    public AbstractSpider setStarterConfiger(String[] urls,CrawlerAction crawlerAction){
        return setStarterConfiger(urls,null, crawlerAction,null);
    }

    /**
     * post请求
     * @param url
     * @param crawlerAction
     * @return
     */
    public AbstractSpider setStarterConfiger(String url,CrawlerAction crawlerAction){
        return setStarterConfiger(url,null, crawlerAction,null);
    }

    /**
     * post请求
     * @param url
     * @param postBody
     * @param crawlerAction
     * @return
     */
    public AbstractSpider setStarterConfiger(String url,Map<String, String> postBody,CrawlerAction crawlerAction){
        return setStarterConfiger(url,postBody,crawlerAction,null);
    }

    /**
     * post请求
     * @param urls
     * @param postBody
     * @param crawlerAction
     * @return
     */
    public AbstractSpider setStarterConfiger(String urls[],Map<String, String> postBody,CrawlerAction crawlerAction){
        return setStarterConfiger(urls,postBody,crawlerAction,null);
    }

    public AbstractSpider setListenHttpStatus(int httpStatus){
        this.startConfiger.setListenHttpStatus(httpStatus);
        return this;
    }

    public ExecutorService getUsingExecutorService() {
        return usingExecutorService;
    }

    public Properties getProperty() {
        return property;
    }

    public CrawlMes getCrawlMes() {
        return crawlMes;
    }

    public void setCrawlMes(CrawlMes crawlMes) {
        this.crawlMes = crawlMes;
    }

    public StartConfiger getStartConfiger() {
        return startConfiger;
    }

    public void setStartConfiger(StartConfiger startConfiger) {
        this.startConfiger = startConfiger;
    }


    public AbstractSpider addCookie(String key,String value,String domain,String path){
        BasicClientCookie cookie = new BasicClientCookie(key,value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        if(startConfiger.cookieStore == null){
            startConfiger.cookieStore = new BasicCookieStore();
        }
        startConfiger.cookieStore.addCookie(cookie);
        return this;
    }
}
