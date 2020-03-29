package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.NormalCrawlMes;

import java.util.Collection;

/**
 * Created by liuwenrui on 2018/3/4
 *
 * Simple spider,which is subclass of abstractspider.
 * To used SimpleSpider,you have to know
 * {@link xyz.iamray.core.SpiderProperty} and {@link xyz.iamray.core.AbstractSpider}.
 *
 *
 */
@Slf4j
public class SimpleSpider extends AbstractSpider{


    private SimpleSpider(){}

    /**
     * 创建
     * @return
     */
    public static SimpleSpider make(){
        return init();
    }

    /**
     * 初始化爬虫，此爬虫不能链式调用的，自行设置StartConfiger
     * 要复用SimpleSpider，需重新设置CrawlMes
     * @return
     */
    private static SimpleSpider init() {
        SimpleSpider spider = new SimpleSpider();
        spider.crawlMes = new NormalCrawlMes();
        return spider;
    }

    protected <T1, T2> T2 serialAction(String url, CrawlerAction<T1, T2> crawlerAction,Class<T1> type) {
        T1 re = HttpClientTool.get(url,
                this.getHeader(),
                this.startConfiger.getListenHttpStatus(),
                this.crawlMes,
                this.startConfiger.getHttpClient(),
                type);
        log.info("Crawling " + type.getName() + " success. Dealing result with your action");
        return crawlerAction.crawl(re, this.crawlMes);
    }

    protected <T1,T2> void asyncAction(String url, CrawlerAction<T1,T2> crawlerAction,Class<T1> type){

        T1 re = HttpClientTool.get(url,
                this.getHeader(),
                this.startConfiger.getListenHttpStatus(),
                this.crawlMes,
                this.startConfiger.getHttpClient(),
                type);
        log.info("Crawling "+type.getName()+" success. Dealing result with your action");

        try{
            if(this.startConfiger.isCollection()){
                this.startConfiger.getBlockingQueue().addAll((Collection)crawlerAction.crawl(re,this.crawlMes));
            }else{
                this.startConfiger.getBlockingQueue().add(crawlerAction.crawl(re,this.crawlMes));
            }
        }catch(Exception e){
            throw new SpiderException(e);
        }
    }


    /**
     * 设置链接属性
     * @param retryTime 默认是一次
     * @param connectTimeout 默认是5秒
     * @return
     */
    public SimpleSpider setRequestConfig(Integer retryTime,Integer connectTimeout){
        this.setRetryTime(retryTime != null && retryTime > 0 ? retryTime : 1);
        this.setConnectTimeout(connectTimeout != null && connectTimeout >100?connectTimeout:5000);
        return this;
    }




}
