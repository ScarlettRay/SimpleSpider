package xyz.iamray.actions;

import org.jsoup.nodes.Document;
import xyz.iamray.action.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.repo.CrawlMes;

import java.util.List;

/**
 * @author liuwenrui
 * @since 2018/12/10
 */
public class AnswerPageImageUrlCrawlAction extends AbstractDocumentCrawlerAction<List<String>>{

    @Override
    public List<String> crawl(Document src, CrawlMes crawlMes) {
        return null;
    }
}
