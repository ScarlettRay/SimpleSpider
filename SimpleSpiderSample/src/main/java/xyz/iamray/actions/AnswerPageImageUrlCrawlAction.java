package xyz.iamray.actions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import xyz.iamray.Answer;
import xyz.iamray.action.impl.AbstractJsonObjectCrawlerAction;
import xyz.iamray.repo.CrawlMes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuwenrui
 * @since 2018/12/10
 */
public class AnswerPageImageUrlCrawlAction extends AbstractJsonObjectCrawlerAction<List<Answer>>{


    @Override
    public List<Answer> crawl(JSONObject src, CrawlMes crawlMes) {
        JSONArray answers = src.getJSONArray("data");
        List<Answer> answerList = new ArrayList<>();
        answers.forEach(e->{
            Answer tmp = new Answer();
             JSONObject answer = (JSONObject)e;
             tmp.setUrl(answer.getString("url"));
             tmp.setCommentCount(answer.getInteger("comment_count"));
             tmp.setContent(answer.getString("content"));
             tmp.setCopyable(answer.getBoolean("is_copyable"));
             tmp.setCreatedTime(new Date(answer.getLong("created_time")));
             tmp.setUpdatedTime(new Date(answer.getLong("updated_time")));
             tmp.setVoteupCount(answer.getInteger("voteup_count"));
             tmp.setName(answer.getJSONObject("author").getString("name"));
             answerList.add(tmp);
        });
        return answerList;
    }
}
