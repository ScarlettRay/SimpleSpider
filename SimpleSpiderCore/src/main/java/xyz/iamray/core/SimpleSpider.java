package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.exception.ExceptionStrategy;
import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.link.Result;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.NormalCrawlMes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    protected <T1,T2> T2 serialCrawl(String url,CrawlerAction<T1,T2> crawlerAction){
        try {
            Future<T2> future = usingExecutorService.submit(()->{
                //外部属性注入
                crawlerAction.setProperty(this.property);
                crawlMes.setCurrentUrl(url);
                //FIXME 不要让它抛警告
                Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass().getSuperclass())[0];
                T1 re = HttpClientTool.get(url,
                        this.getHeader(),
                        this.crawlMes,
                        this.startConfiger.getHttpClient(),
                        type);
                log.info("Crawling "+type.getName()+" success. Dealing result with your action");
                try {
                    return crawlerAction.crawl(re,this.crawlMes);
                }catch (Exception e){
                    throw new SpiderException(e);
                }

            });
            return future.get();
        } catch (SpiderException se){
            //FIXME 异常处理机制
            int s = this.getExceptionStrategy().dealWithException(se,crawlMes);
            //对爬虫线程的后续处理
            if(s == ExceptionStrategy.RETRY){
                //retry in limit times
                if(crawlMes.increamentAndGetRetryTime()<3){
                    return serialCrawl(url,crawlerAction);
                }
            }else if(s == ExceptionStrategy.BREAKOUT){
                //do nothing;
            }else if(s == ExceptionStrategy.IGNORE){
                //do nothing;
            }
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T1,T2> void asyncCrawl(String url,CrawlerAction<T1,T2> crawlerAction){
        usingExecutorService.execute(()->{
            //外部属性注入
            crawlerAction.setProperty(this.property);
            this.crawlMes.setCurrentUrl(url);

            Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass().getSuperclass())[0];
            T1 re = null;
            try{
                re = HttpClientTool.get(url,
                        this.getHeader(),
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

            }catch (SpiderException se){
                //FIXME 异常处理机制
                int s = this.getExceptionStrategy().dealWithException(se,crawlMes);
                //对爬虫线程的后续处理
                if(s == ExceptionStrategy.RETRY){
                    //retry in limit times
                    if(crawlMes.increamentAndGetRetryTime()<3){
                        asyncCrawl(url,crawlerAction);
                    }
                }else if(s == ExceptionStrategy.BREAKOUT){
                    //do nothing;
                }else if(s == ExceptionStrategy.IGNORE){
                    //do nothing;
                }
            }


        });
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


    private <T1,T2> List<T2> crawlBundle(String[] urls,CrawlerAction<T1,T2> crawlerAction){
        if(urls == null || urls.length == 0)Assert.fail("urls can not be null!");
        //boolean isCollection = SpiderUtil.isArgumentsCollectionInSuperClass(crawlerAction,1);
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
     * @param <T2>
     * @return
     */
    @Override
    public <T1,T2> Result<T2> start(){
        CrawlerAction<T1,T2> crawlerAction = startConfiger.getCrawlerAction();
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            List<T2> list = crawlBundle(this.startConfiger.getUrls(),crawlerAction);
            return new Result<>(list,this.crawlMes);
        }else{
            T2 obj = serialCrawl(this.startConfiger.getUrl(),crawlerAction);
            return new Result<>(obj,this.crawlMes);
        }

    }

    @Override
    public <T1, T2> Result<T2> start(BlockingQueue<T2> blockingQueue) {
        this.startConfiger.setBlockingQueue(blockingQueue);
        CrawlerAction<T1,T2> crawlerAction = startConfiger.getCrawlerAction();
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            crawlBundle(this.startConfiger.getUrls(),crawlerAction);
        }else{
            asyncCrawl(this.startConfiger.getUrl(),crawlerAction);
        }
        return null;
    }


}
