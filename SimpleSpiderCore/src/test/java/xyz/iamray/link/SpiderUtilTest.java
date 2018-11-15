package xyz.iamray.link;

import com.alibaba.fastjson.JSON;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.action.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.repo.CrawlMes;

import java.util.List;
import java.util.Map;

public class SpiderUtilTest {

    @Test
    public void isArgumentsCollection() {
        Assert.assertTrue(SpiderUtil.isArgumentsCollection(new TestJsonCrawlerAction(),0));
        Assert.assertFalse(SpiderUtil.isArgumentsCollection(new TestDocumentCrawlerActio(),0));

        Assert.assertTrue(SpiderUtil.isArgumentsCollection(new TestTowType(),0));
        Assert.assertTrue(SpiderUtil.isArgumentsCollection(new TestTowType(),1));
    }

    @Test
    public void getClassArguments() {
    }

    @Test
    public void TestgetClass() {

    }

    class TestJsonCrawlerAction extends AbstractJsonCrawlerAction<List<String>>{

        @Override
        public List<String> crawl(JSON t, CrawlMes crawlMes) {
            return null;
        }
    }

    class TestDocumentCrawlerActio extends AbstractDocumentCrawlerAction<String>{

        @Override
        public String crawl(Document t, CrawlMes crawlMes) {
            return null;
        }
    }

    interface TestInterface<T1,T2>{}

    class TestTowType implements TestInterface<List<String>,Map<String,String>>{

    }
}