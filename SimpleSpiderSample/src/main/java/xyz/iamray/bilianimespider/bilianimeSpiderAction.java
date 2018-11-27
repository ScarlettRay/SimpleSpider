package xyz.iamray.bilianimespider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import xyz.iamray.action.impl.AbstractJsonCrawlerAction;
import xyz.iamray.bilianimespider.bean.Anime;
import xyz.iamray.repo.CrawlMes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuwenrui on 2018/5/5
 */
public class bilianimeSpiderAction extends AbstractJsonCrawlerAction<List<Anime>> {

    @Override
    public List<Anime> crawl(JSON t, CrawlMes crawlMes) {
        JSONObject jsonObject = (JSONObject)t;
        JSONArray animeList = jsonObject.getJSONObject("result").getJSONArray("list");
        List<Anime> animes = new ArrayList<>();
        animeList.forEach(e->{
            JSONObject anime = (JSONObject)e;
            animes.add(new Anime(
                    anime.getString("title"),
                    anime.getInteger("favorites"),
                    anime.getInteger("is_finish"),
                    anime.getInteger("newest_index"),
                    anime.getString("badge"),
                    new Date(anime.getLong("pub_time")*1000),
                    new Date(anime.getLong("update_time")*1000),
                    anime.getInteger("total_count"),
                    anime.getInteger("season_id"),
                    anime.getString("url"),
                    anime.getInteger("week"),
                    anime.getString("cover")
            ));
        });

        return animes;
    }
}
