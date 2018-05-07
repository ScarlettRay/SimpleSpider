package xyz.iamray.bilianimespider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import xyz.iamray.api.impl.AbstractDocumentCrawlerAction;
import xyz.iamray.bilianimespider.bean.Anime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuwenrui on 2018/5/5
 */
public class AnimeDetailSpiderAction extends AbstractDocumentCrawlerAction<Anime> {

    private Pattern scriptePattern = Pattern.compile("(?<=window.__INITIAL_STATE__=).*?(?=;)");
    @Override
    public Anime documentCrawl(Document document, String url) {
        Anime anime = getAttr("anime",Anime.class);
        Matcher matcher = scriptePattern.matcher(document.html());
        JSONObject detailJson = null;
        if(matcher.find()){
            detailJson = JSONObject.parseObject(matcher.group(0));
        }
        if(detailJson != null){
           JSONObject mediaInfo = detailJson.getJSONObject("mediaInfo");

           anime.setContent(mediaInfo.getString("evaluate"));

           JSONArray style = mediaInfo.getJSONArray("style");
           List<String> tagList = new ArrayList<>();
           anime.setTagList(tagList);
           style.forEach(e->{
               JSONObject tag = (JSONObject)e;
               tagList.add(((JSONObject) e).getString("name"));
           });

           //评分
            anime.setScore(mediaInfo.getJSONObject("rating").getFloat("score"));
            anime.setScorecount(mediaInfo.getJSONObject("rating").getInteger("count"));

            JSONArray area  = mediaInfo.getJSONArray("area");
            List<String> areaList = new ArrayList<>();
            area.forEach(e->{
                JSONObject a = (JSONObject)e;
                areaList.add(a.getString("name"));
            });
            anime.setArea(areaList);

            anime.setDanmu(mediaInfo.getJSONObject("stat").getInteger("danmakus"));
            anime.setPlaynum(mediaInfo.getJSONObject("stat").getInteger("views"));
        }
        return anime;
    }
}
