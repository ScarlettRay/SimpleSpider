package xyz.iamray;

import org.junit.Test;
import xyz.iamray.actions.AnimeAction;
import xyz.iamray.core.SimpleSpider;
import xyz.iamray.link.Result;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liuwenrui
 * @since 2018/11/27
 * 抓取速度测试
 */
public class SpeedTest {

    private String jsonUrl = "" +
            "https://bangumi.bilibili.com/media/web_api/search/result?season_version=-1&area=-1&is_finish=-1&copyright=-1&season_status=-1&season_month=-1&pub_date=-1&style_id=-1&order=3&st=1&sort=0&page=1&season_type=1&pagesize=20";

    private String[] urls = new String[]{
            jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl,jsonUrl};

    @Test
    public void testInWhileLoop(){
        long pos = System.currentTimeMillis();
        for(int i =0;i<10;i++){
            Result<List<String>> re = SimpleSpider.make().defaultThreadPool()
                    .setStarterConfiger(jsonUrl,new AnimeAction()).start();
        }
        System.out.println(System.currentTimeMillis()-pos);
    }

    @Test
    public void testInSync(){
        long pos = System.currentTimeMillis();
        BlockingQueue<List<String>> queue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Properties pro = new Properties();
        pro.put("count",countDownLatch);
        SimpleSpider.make().defaultThreadPool()
                .setProperty(pro)
                .setStarterConfiger(urls,new AnimeAction())
                .setBlockingQueue(queue).start();
        try {
            countDownLatch.await();
            System.out.println(System.currentTimeMillis()-pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
