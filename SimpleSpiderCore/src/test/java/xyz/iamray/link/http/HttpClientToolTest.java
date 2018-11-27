package xyz.iamray.link.http;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import xyz.iamray.core.SpiderConstant;

import java.util.HashMap;
import java.util.Map;


public class HttpClientToolTest {

    private String url = "https://www.baidu.com/";

    private String wrongUrl  = "https://www.baidus.com/";

    String postUrl = "https://weibo.com/aj/onoff/setstatus?ajwvr=6";
    Map<String,String> header = new HashMap<>();

    Map<String,String> postbody = new HashMap<>();
    {
        header.put("User-Agent",SpiderConstant.ChromeUserAgent);
        header.put("Referer","https://weibo.com/u/5945738590/home?wvr=5");
        postbody.put("sid","0");
        postbody.put("state","0");
    }


    @Test
    public void get(){
        Assert.assertNotNull(HttpClientTool.get(url, SpiderConstant.DefaultHeader,HttpClientPool.getHttpClient(), Document.class));
        //Assert.assertNull(HttpClientTool.get(wrongUrl, SpiderConstant.DefaultHeader,HttpClientPool.getHttpClient(), Document.class));
    }

    @Test
    public void defultGet() {
    }

    @Test
    public void post() {
        //Assert.assertNotNull(HttpClientTool.post(url,SpiderConstant.DefaultHeader,new HashMap<>(),HttpClientTool.getHttpClient(),Document.class));
    }

    @Test
    public void defaultPost() {

    }
}