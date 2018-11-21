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
        header.put("Cookie","SINAGLOBAL=84752659228.94603.1517993264194; UOR=,,login.sina.com.cn; Ugrow-G0=e66b2e50a7e7f417f6cc12eec600f517; SSOLoginState=1542272649; YF-V5-G0=b8115b96b42d4782ab3a2201c5eba25d; YF-Page-G0=e44a6a701dd9c412116754ca0e3c82c3; _s_tentry=login.sina.com.cn; Apache=6620278351488.687.1542272653168; ULV=1542272653223:19:2:2:6620278351488.687.1542272653168:1542011704160; TC-V5-G0=866fef700b11606a930f0b3297300d95; TC-Page-G0=42b289d444da48cb9b2b9033b1f878d9; wvr=6; SCF=AmrCcwLRiRP9VntqX95fSDa81Xohv5kpDgPjP4xRwS5cnsCbRLQbOnZ0KBSeTUNDHcv06PPpVDi8pEXksQWONpQ.; SUB=_2A2528TSKDeRhGeNH71cW8ybJwjyIHXVVhyFCrDV8PUJbmtAKLVLFkW9NSrjjsgnJvCKn06zdC_I-mM6sSQDC-00R; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWNOfpqzm0R08d3Q2m.zYlV5JpX5K-hUgL.Fo-4Sh-Ne0nf1K52dJLoI0qLxK-LBo5L1K2LxK-LBK-LBoeLxK-L1hqL1K.LxKML1-eL1-qLxKqL12-LBKnLxKnL12BL1K5t; SUHB=0P1cmaSmuUlICn; ALF=1574336600; wb_view_log_5945738590=1440*9002");
        header.put("User-Agent",SpiderConstant.ChromeUserAgent);
        header.put("Referer","https://weibo.com/u/5945738590/home?wvr=5");
        postbody.put("sid","0");
        postbody.put("state","0");
    }


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
        Assert.assertNotNull(HttpClientTool.post(url,SpiderConstant.DefaultHeader,new HashMap<>(),HttpClientTool.getHttpClient(),Document.class));
    }

    @Test
    public void defaultPost() {

    }
}