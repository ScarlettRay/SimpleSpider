package xyz.iamray.starter;

import org.junit.jupiter.api.Test;
import xyz.iamray.Answer;
import xyz.iamray.actions.AnswerPageImageUrlCrawlAction;
import xyz.iamray.common.UrlConstant;
import xyz.iamray.core.SimpleSpider;
import xyz.iamray.link.Result;

import java.util.List;

/**
 * @author liuwenrui
 * @since 2018/12/10
 */
public class TestStarter {

    @Test
    public void firstTest(){
       Result<List<Answer>> result = SimpleSpider.make().defaultThreadPool()
                .setStarterConfiger(UrlConstant.WALLPAPER_URL.replace("{offset}","0").replace("{limit}","5"),new AnswerPageImageUrlCrawlAction())
                .start();
        for (Answer answer : result.getObj()) {
            System.out.println(answer);
        }

    }
}
