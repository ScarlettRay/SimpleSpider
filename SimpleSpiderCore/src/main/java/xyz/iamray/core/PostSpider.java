package xyz.iamray.core;

import lombok.extern.slf4j.Slf4j;
import xyz.iamray.action.CrawlerAction;
import xyz.iamray.link.http.HttpClientTool;
import xyz.iamray.repo.NormalCrawlMes;

/**
 * @author winray
 * @since v1.0.1
 * 只针对post的方法
 */
@Slf4j
public class PostSpider extends AbstractSpider {

    public static PostSpider make(){
        PostSpider spider = new PostSpider();
        spider.crawlMes = new NormalCrawlMes();
        return spider;
    }

    protected <T1,T2> T2 serialAction(String url, CrawlerAction<T1,T2> crawlerAction,Class<T1> type){
        T1 re = HttpClientTool.post(url,
                this.getHeader(),
                this.startConfiger.getListenHttpStatus(),
                this.startConfiger.getPostBody(),
                this.crawlMes,
                this.startConfiger.getHttpClient(), type);
        log.info("Crawling " + type.getName() + " success. Dealing result with your action");
        return crawlerAction.crawl(re, this.crawlMes);
    }

    @Override
    protected <T1, T2> void asyncAction(String url, CrawlerAction<T1, T2> crawlerAction, Class<T1> type) {
        throw new UnsupportedOperationException("PostSpider不支持异步");
    }

}
