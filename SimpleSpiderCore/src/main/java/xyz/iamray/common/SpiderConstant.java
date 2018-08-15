package xyz.iamray.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwenrui on 2018/3/9
 * simplespider 的常量
 */
public class SpiderConstant {
    //User-agent 伪装成chrome
    public static final String ChromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
    //微博爬虫代理 直接暴露为爬虫
    public static final String SpiderUserAgent = "spider";

    public final static Map<String,String> DefaultHeader = new HashMap<>();

    public final static Map<String,String> SpiderHeader = new HashMap<>();

    public final static String UTF8 = "UTF-8";

    public final static String GBK = "GBK";

    static{
        DefaultHeader.put("User-Agent",ChromeUserAgent);
        SpiderHeader.put("User-Agent",SpiderUserAgent);
    }
}
