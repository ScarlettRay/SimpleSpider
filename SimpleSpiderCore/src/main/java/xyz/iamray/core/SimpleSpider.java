package xyz.iamray.core;

import com.alibaba.fastjson.JSON;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.action.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.utils.http.HttpClientTool;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by liuwenrui on 2018/3/4
 * 我的爬虫框架
 */
public class SimpleSpider {

    protected Logger logger = LoggerFactory.getLogger(getClass());



    /**
     * 待爬取的URL
     */
    private String url;
    private String[] urls;

    /**
     * 用户的HttpClient,用于维护一段会话
     */
    private CloseableHttpClient httpClient;

    /**
     * 阻塞队列
     */
    private BlockingQueue blockingQueue;

    /**
     * 是否异步执行的旗帜
     */
    private boolean asyncFlag = false;


    /**
     * 创建一个例子
     * @return
     */
    public static SimpleSpider make(){
        return new SimpleSpider();
    }


    /**
     * 自定义线程池
     * @param core 核心线程数
     * @return
     */
    public SimpleSpider cumstomizeThreadPool(Integer core){
        cumstomizeExecutorService  = SimpleSpider.myInitThreadPool(core,null,null);
        usingExecutorService = this.cumstomizeExecutorService;
        return this;
    }

    /**
     * 更多参数的初始化线程池的方法
     * @param coreNum
     * @param maxNum
     * @param keepALiveTime
     * @return
     */
    public SimpleSpider anotherCumstomizeThreadPool(Integer coreNum,Integer maxNum,Long keepALiveTime){
        cumstomizeExecutorService  = SimpleSpider.myInitThreadPool(coreNum,maxNum,keepALiveTime);
        usingExecutorService = this.cumstomizeExecutorService;
        return this;
    }

    public SimpleSpider myThreadPool(ExecutorService myThreadPoolExecutor){
        cumstomizeExecutorService = myThreadPoolExecutor;
        usingExecutorService = cumstomizeExecutorService;
        return this;
    }

    /**
     * 设置请求头部
     * @param header
     * @return
     */
    public SimpleSpider setHeader(Map<String,String> header){
        this.header = header;
        return this;
    }


    /**
     * 自己的初始化线程池的方法
     * @param coreNum
     * @param maxNum
     * @param KeepAliveTime
     * @return
     */
    private static ExecutorService myInitThreadPool(Integer coreNum,Integer maxNum,Long KeepAliveTime){

        int localMaxNum = maxNum == null || maxNum < coreNum ? coreNum: maxNum;
        long localKeepAliveTime = KeepAliveTime == null? 0L:KeepAliveTime;

        return new ThreadPoolExecutor(coreNum, localMaxNum,
                localKeepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 设置链接属性
     * @param retryTime 默认是一次
     * @param connectTimeout 默认是5秒
     * @return
     */
    public SimpleSpider setRequestConfig(Integer retryTime,Integer connectTimeout){
        this.retryTime = retryTime != null && retryTime > 0 ? retryTime : 1;
        this.connectTimeout = connectTimeout != null && connectTimeout >100?connectTimeout:5000;
        return this;
    }

    public <T> T getAttr(String key, Class T) {
        if(property == null){
            throw new NullPointerException();
        }
        return (T)property.get(key);
    }

    public SimpleSpider setProperty(Properties property){
        this.property = property;
        return this;
    }

    /**
     * 爬取的URL设置
     * @param url
     * @param httpClient
     * @return
     */
    public SimpleSpider crawlURL(String url, CloseableHttpClient httpClient){
        Assert.assertNotNull("url can not be null! ",url);
        this.url = url;
        if(httpClient != null){
            this.httpClient = httpClient;
        }else{
            this.httpClient = HttpClientTool.getCumstomizedHttpClient(this.retryTime,this.connectTimeout);
        }
        return this;
    }

    public SimpleSpider crawlURLS(String[] urls,CloseableHttpClient httpClient){
        if(urls == null || urls.length == 0)Assert.fail("urls is invalid");
        this.urls = urls;
        if(httpClient != null){
            this.httpClient = httpClient;
        }else{
            this.httpClient = HttpClientTool.getCumstomizedHttpClient(this.retryTime,this.connectTimeout);
        }
        return this;
    }

    /**
     * 异步执行的方法
     * @param blockingQueue
     * @return
     */
    public SimpleSpider async(BlockingQueue blockingQueue){
        Assert.assertNotNull("异步执行时的blockingDeque不允许为 null ",blockingQueue);

        this.blockingQueue = blockingQueue;
        this.asyncFlag  = true;

        return this;
    }

    /**
     * 启动函数
     * @param crawlerAction
     * @param <T>
     * @return
     */
    public <T> T crawl(CrawlerAction<T> crawlerAction){
        Assert.assertNotNull("url can not be null!",this.url);
        boolean isCollection = iamray.utils.MyUtil.isArgumentsCollection(crawlerAction);
        if(this.asyncFlag){
            asyncCrawl(this.url,crawlerAction,isCollection);
        }else{
            return awaitCrawl(this.url,crawlerAction);
        }
        return null;
    }

    public <T> List<T> crawlBundle(CrawlerAction<T> crawlerAction){
        if(this.urls == null || this.urls.length == 0)Assert.fail("urls can not be null!");
        boolean isCollection = iamray.utils.MyUtil.isArgumentsCollection(crawlerAction);
        if(this.asyncFlag){
            for(String s:this.urls){
                asyncCrawl(this.url,crawlerAction,isCollection);
            }
        }else{
            List<T> result = new ArrayList<>();
            for (String s : this.urls) {
                result .add(awaitCrawl(this.url,crawlerAction));
            }
            return result;
        }
        return null;
    }

    private <T> T awaitCrawl(String url,CrawlerAction<T> crawlerAction){
        Future<T> future = usingExecutorService.submit(()->{
            //属性注入
            crawlerAction.setProperty(this.property);

            if(crawlerAction instanceof AbstractDocumentCrawlerAction){
                logger.debug("正在爬取网页: "+url);
                Document document = HttpClientTool.getDocumentWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);

                return  crawlerAction.documentCrawl(document,url);
            }else if(crawlerAction instanceof AbstractJsonCrawlerAction){
                logger.debug("正在爬取json: "+url);
                JSON json = HttpClientTool.getJSONWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);

                return crawlerAction.JSONCrawl(json,url);
            }else{
                logger.debug("正在爬取文件:"+url);
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

    /**
     * 异步爬取
     */
    private void asyncCrawl(String url,CrawlerAction crawlerAction,boolean isCollection){
        usingExecutorService.execute(()->{
            //属性注入
            crawlerAction.setProperty(this.property);

            if(crawlerAction instanceof AbstractDocumentCrawlerAction){
                logger.debug("正在爬取网页: "+url);
                Document document = HttpClientTool.getDocumentWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);
                if(isCollection){
                    this.blockingQueue.addAll((Collection) crawlerAction.documentCrawl(document,url));
                }else{
                    this.blockingQueue.add(crawlerAction.documentCrawl(document,url));
                }
            }else if(crawlerAction instanceof AbstractJsonCrawlerAction){
                logger.debug("正在爬取json: "+url);
                JSON json = HttpClientTool.getJSONWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);
                if(isCollection){
                    this.blockingQueue.addAll((Collection) crawlerAction.JSONCrawl(json,url));
                }else{
                    this.blockingQueue.add(crawlerAction.JSONCrawl(json,url));
                }
            }else{
                logger.debug("正在爬取文件:"+url);
                byte[] bytes = HttpClientTool.getBytesWithHttpClient(
                        url,
                        this.header,
                        this.httpClient);
                if(isCollection){
                    this.blockingQueue.addAll((Collection) crawlerAction.FileCrawl(bytes,url));
                }else{
                    this.blockingQueue.add(crawlerAction.FileCrawl(bytes,url));
                }
            }
        });
    }


}
