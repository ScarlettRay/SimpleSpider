package xyz.iamray.actions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import xyz.iamray.action.impl.AbstractJsonObjectCrawlerAction;
import xyz.iamray.repo.CrawlMes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuwenrui
 * @since 2018/11/27
 */
public class AnimeAction extends AbstractJsonObjectCrawlerAction<List<String>> {
    @Override
    public List<String> crawl(JSONObject t, CrawlMes crawlMes) {
        CountDownLatch count = null;//getAttr("count",CountDownLatch.class);
        if(count != null){
            count.countDown();
        }
        JSONArray array = t.getJSONObject("result").getJSONArray("data");
        List<String> re = new ArrayList<>();
        array.forEach(e->{
            JSONObject tmp = (JSONObject)e;
            re.add(tmp.getString("title"));
        });
        return re;
    }
}
