package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.link.Result;
import xyz.iamray.repo.NormalCrawlMes;

import java.util.ArrayList;
import java.util.List;

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


    /**
     * 创建一个例子
     * @return
     */
    public static SimpleSpider make(){
        return init();
    }

    private static SimpleSpider init() {
        SimpleSpider spider = new SimpleSpider();
        spider.crawlMes = new NormalCrawlMes();
        return spider;
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


    public <T1,T2> List<T2> crawlBundle(String[] urls,CrawlerAction<T1,T2> crawlerAction){
        if(urls == null || urls.length == 0)Assert.fail("urls can not be null!");
        //boolean isCollection = SpiderUtil.isArgumentsCollection(crawlerAction,1);
        if(this.startConfiger.getBlockingQueue() != null){
            for(String url:urls){
                asyncCrawl(url,crawlerAction);
            }
        }else{
            List<T2> result = new ArrayList<>();
            for (String url : urls) {
                result.add(serialCrawl(url,crawlerAction));
            }
            return result;
        }
        return null;
    }

    /**
     * Method to start spider
     * FIXME
     * @param <T>
     * @return
     */
    public <T> Result<T> start(){
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            List<T> list = crawlBundle(this.startConfiger.getUrls(),this.startConfiger.getCrawlerAction());
            return new Result<>(list,this.crawlMes);
        }
        if(this.startConfiger.getBlockingQueue() != null){
            asyncCrawl(this.startConfiger.getUrl(),this.startConfiger.getCrawlerAction());
            return null;
        }else{
           T obj = serialCrawl(this.startConfiger.getUrl(),this.startConfiger.getCrawlerAction());
           return new Result<>(obj,this.crawlMes);
        }
    }





}
