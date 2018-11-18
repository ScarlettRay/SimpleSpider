package xyz.iamray.link.http;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.core.SpiderConstant;


public class HttpClientToolTest {

    private String url = "https://www.baidu.com/";

    private String wrongUrl  = "https://www.baidus.com/";

    @Test
    public void get(){
        Assert.assertNotNull(HttpClientTool.get(url, SpiderConstant.DefaultHeader,HttpClientPool.getHttpClient(), Document.class));
        Assert.assertNull(HttpClientTool.get(wrongUrl, SpiderConstant.DefaultHeader,HttpClientPool.getHttpClient(), Document.class));
    }

    @Test
    public void defultGet() {
    }

    @Test
    public void post() {
    }

    @Test
    public void defaultPost() {
    }
}