package xyz.iamray.core;

import org.junit.Assert;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.exception.ExceptionStrategy;
import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.link.Result;
import xyz.iamray.link.SpiderUtil;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.NormalCrawlMes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author winray
 * @since v1.0.1
 * 只针对post的方法
 */
public class PostSpider extends AbstractSpider {

    public static PostSpider make(){
        PostSpider spider = new PostSpider();
        spider.crawlMes = new NormalCrawlMes();
        return spider;
    }

    protected <T1,T2> T2 serialPost(String url, CrawlerAction<T1,T2> crawlerAction){
        try {
            Future<T2> future = usingExecutorService.submit(() -> {
                //外部属性注入
                crawlerAction.setProperty(this.property);
                crawlMes.setCurrentUrl(url);
                //FIXME 不要让它抛警告
                Class<T1> type = SpiderUtil.getClass(crawlerAction.getClass().getSuperclass())[0];
                T1 re = HttpClientTool.post(url,
                        this.getHeader(),
                        this.startConfiger.getListenHttpStatus(),
                        this.startConfiger.getPostBody(),
                        this.crawlMes,
                        this.startConfiger.getHttpClient(), type);
                try {
                    return crawlerAction.crawl(re, this.crawlMes);
                } catch (Exception e) {
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
                    return serialPost(url,crawlerAction);
                }
            }else if(s == ExceptionStrategy.BREAKOUT){
                //do nothing;
            }else if(s == ExceptionStrategy.IGNORE){
                //do nothing;
            }
        } catch (InterruptedException e) {
            throw new SpiderException(e);
        } catch (ExecutionException e) {
            throw new SpiderException(e);
        }
        return null;
    }

    private <T1,T2> List<T2> postBundle(String[] urls,CrawlerAction<T1,T2> crawlerAction){
        if(urls == null || urls.length == 0) Assert.fail("urls can not be null!");
        //boolean isCollection = SpiderUtil.isArgumentsCollectionInSuperClass(crawlerAction,1);
        if(this.startConfiger.getBlockingQueue() != null){
            throw new UnsupportedOperationException("不支持异步");
        }else{
            List<T2> result = new ArrayList<>();
            for (String url : urls) {
                result.add(serialPost(url,crawlerAction));
            }
            return result;
        }
    }


        @Override
    public <T1, T2> Result<T2> start() {
        CrawlerAction<T1,T2> crawlerAction = startConfiger.getCrawlerAction();
        if(this.startConfiger.getUrls() != null
                && this.startConfiger.getUrls().length > 0){
            List<T2> list = postBundle(this.startConfiger.getUrls(),crawlerAction);
            return new Result<>(list,this.crawlMes);
        }else{
            T2 obj = serialPost(this.startConfiger.getUrl(),crawlerAction);
            return new Result<>(obj,this.crawlMes);
        }
    }


    @Override
    public <T1, T2> Result<T2> start(BlockingQueue<T2> blockingQueue) {
        throw new UnsupportedOperationException("不支持异步");
    }
}
