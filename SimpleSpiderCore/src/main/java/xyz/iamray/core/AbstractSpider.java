package xyz.iamray.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.Assert;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.exception.ExceptionStrategy;
import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.link.Result;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.CrawlMes;

import java.util.*;
import java.util.concurrent.*;

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
    protected HashMap<String,Object> pro = new HashMap<>();


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

    public AbstractSpider setProperty(String key,Object value){
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

    protected abstract  <T1,T2> T2 serialAction(String url,CrawlerAction<T1,T2> crawlerAction,Class<T1> type);

    /**
     * 串行请求
     * @param url
     * @param crawlerAction
     * @param <T1>
     * @param <T2>
     * @return
     */
    protected <T1,T2> T2 serial(String url,CrawlerAction<T1,T2> crawlerAction){
        try {
            Future<T2> future = usingExecutorService.submit(()-> {
                //外部属性注入
                crawlerAction.setProperty(this.property);
                crawlerAction.setProperty(this.pro);
                crawlMes.setCurrentUrl(url);
                Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass().getSuperclass())[0];
                return serialAction(url,crawlerAction,type);
            });
            return future.get();
        } catch (Exception se){
            //FIXME 异常处理机制
            int s = this.getExceptionStrategy().dealWithException(se,crawlMes);
            //对爬虫线程的后续处理
            if(s == ExceptionStrategy.RETRY){
                //retry in limit times
                if(crawlMes.increamentAndGetRetryTime()<3){
                    return serial(url,crawlerAction);
                }
            }else if(s == ExceptionStrategy.BREAKOUT){
                //do nothing;
            }else if(s == ExceptionStrategy.IGNORE){
                //do nothing;
            }else{
                throw new SpiderException("不支持的异常处理策略");
            }
        }
        return null;
    }

    protected abstract <T1,T2> void asyncAction(String url, CrawlerAction<T1,T2> crawlerAction,Class<T1> type);

    /**
     * 并发请求
     * @param url
     * @param crawlerAction
     * @param <T1>
     * @param <T2>
     */
    protected <T1,T2> void async(String url,CrawlerAction<T1,T2> crawlerAction){
        usingExecutorService.execute(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);
            crawlerAction.setProperty(this.pro);
            this.crawlMes.setCurrentUrl(url);

            Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass().getSuperclass())[0];
            try{
                asyncAction(url,crawlerAction,type);
            }catch (Exception se){
                //FIXME 异常处理机制
                int s = this.getExceptionStrategy().dealWithException(se,crawlMes);
                //对爬虫线程的后续处理
                if(s == ExceptionStrategy.RETRY){
                    //retry in limit times
                    if(crawlMes.increamentAndGetRetryTime()<3){
                        async(url,crawlerAction);
                    }
                }else if(s == ExceptionStrategy.BREAKOUT){
                    //do nothing;
                }else if(s == ExceptionStrategy.IGNORE){
                    //do nothing;
                }else{
                    throw new SpiderException("不支持的异常处理策略");
                }
            }
        });
    }

    private <T1,T2> List<T2> bundle(String[] urls,CrawlerAction<T1,T2> crawlerAction){
        if(urls == null || urls.length == 0) Assert.fail("urls can not be null!");
        //boolean isCollection = SpiderUtil.isArgumentsCollectionInSuperClass(crawlerAction,1);
        if(this.startConfiger.getBlockingQueue() != null){
            for(String url:urls){
                async(url,crawlerAction);
            }
        }else{
            List<T2> result = new ArrayList<>();
            for (String url : urls) {
                result.add(serial(url,crawlerAction));
            }
            return result;
        }
        return null;
    }

    /**
     * Method to start spider
     * FIXME
     * @param <T2>
     * @return
     */
    @Override
    public <T1,T2> Result<T2> start(){
        CrawlerAction<T1,T2> crawlerAction = startConfiger.getCrawlerAction();
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            List<T2> list = bundle(this.startConfiger.getUrls(),crawlerAction);
            return new Result<>(list,this.crawlMes);
        }else{
            T2 obj = serial(this.startConfiger.getUrl(),crawlerAction);
            return new Result<>(obj,this.crawlMes);
        }

    }

    @Override
    public <T1, T2> Result<T2> start(BlockingQueue<T2> blockingQueue) {
        this.startConfiger.setBlockingQueue(blockingQueue);
        CrawlerAction<T1,T2> crawlerAction = startConfiger.getCrawlerAction();
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            bundle(this.startConfiger.getUrls(),crawlerAction);
        }else{
            async(this.startConfiger.getUrl(),crawlerAction);
        }
        return null;
    }


}
