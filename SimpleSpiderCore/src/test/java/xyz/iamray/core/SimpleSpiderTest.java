package xyz.iamray.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.link.Result;
import xyz.iamray.repo.CrawlMes;

import java.util.ArrayList;
import java.util.List;

public class SimpleSpiderTest {

    private String jsonUrl = "https://bangumi.bilibili.com/media/web_api/search/result?season_version=-1&area=-1&is_finish=-1&copyright=-1&season_status=-1&season_month=-1&pub_date=-1&style_id=-1&order=3&st=1&sort=0&page=1&season_type=1&pagesize=20";

    private String documentUrl = "https://www.bilibili.com/ranking?spm_id_from=666.14.b_62616e6e65725f6c696e6b.11";

    @Test
    public void make() {
        Assert.assertTrue(SimpleSpider.make() instanceof SimpleSpider);
    }

    @Test
    public void setRequestConfig() {
    }

    @Test
    public void crawlBundle() {
    }

    @Test
    public void start() {
        Result<List<String>> result = SimpleSpider.make().defaultThreadPool()
                .setStarterConfiger(jsonUrl,new AnimeAction()).start();
        System.out.println(result.getObj());
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