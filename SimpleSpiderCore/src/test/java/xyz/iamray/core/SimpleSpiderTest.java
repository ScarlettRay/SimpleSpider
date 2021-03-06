package xyz.iamray.core;

import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.actions.AnimeAction;
import xyz.iamray.link.Result;

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
        String[] urls = new String[]{jsonUrl,jsonUrl};
        Result<List<String>> result = SimpleSpider.make().defaultThreadPool()
                .setStarterConfiger(urls,new AnimeAction()).start();
        System.out.println(result.getObjList());
    }

    @Test
    public void start() {
        Result<List<String>> result = SimpleSpider.make().defaultThreadPool()
                .setStarterConfiger(jsonUrl,new AnimeAction()).start();
        System.out.println(result.getObj());
    }

}