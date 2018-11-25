package xyz.iamray.link;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.action.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.repo.CrawlMes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpiderUtilTest {

    @Test
    public void isArgumentsCollection() {
        Assert.assertTrue(SpiderUtil.isArgumentsCollectionInSuperClass(new TestJsonCrawlerAction(),0));
        Assert.assertFalse(SpiderUtil.isArgumentsCollectionInSuperClass(new TestDocumentCrawlerActio(),0));

        //Assert.assertTrue(SpiderUtil.isArgumentsCollectionInSuperClass(new TestTowType(),0));
        //Assert.assertTrue(SpiderUtil.isArgumentsCollectionInSuperClass(new TestTowType(),1));
    }

    @Test
    public void getClassArguments() {
        String[] classes = new String[]{"java.util.List<java.lang.String>","java.util.Map<java.lang.String, java.lang.String>"};
        //Assert.assertArrayEquals(classes,SpiderUtil.getClassArguments(TestTowType.class));

    }

    @Test
    public void TestgetClass() {
        Class[] classes =  new Class[]{
                List.class,Map.class
        };
       // Assert.assertArrayEquals(classes,SpiderUtil.getClass(TestTowType.class));
        /*
        AnimeAction action = new AnimeAction();
        System.out.println(SpiderUtil.getClass(action.getClass().getSuperclass())[0]);
        */
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

     class Testinterface0<T1,T2>{}

     class TestInterface<T1,T2> extends Testinterface0<JSON,Document>{}


     class TestTowType extends TestInterface<Testinterface0<String,String>,Map<String,String>>{

    }


    class AnimeAction extends AbstractJsonCrawlerAction<List<String>>{

        @Override
        public List<String> crawl(JSON t, CrawlMes crawlMes) {
            JSONObject jsonObject = (JSONObject)t;
            JSONArray array = jsonObject.getJSONObject("result").getJSONArray("data");
            List<String> re = new ArrayList<>();
            array.forEach(e->{
                JSONObject tmp = (JSONObject)e;
                re.add(tmp.getString("title"));
            });
            return re;
        }
    }
}