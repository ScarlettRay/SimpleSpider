package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import xyz.iamray.action.CrawlerAction;

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
        return new SimpleSpider();
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
        if(this.getBlockingQueue() != null){
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



}
