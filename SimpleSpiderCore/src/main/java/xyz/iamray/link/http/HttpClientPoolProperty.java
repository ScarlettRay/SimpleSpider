package xyz.iamray.link.http;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public class HttpClientPoolProperty {


    static final int retryTime = 5;//重试次数

    static final int connectionRequestTimeout = 20000;
    static final int connectTimeout = 5000;
    static final int socketTimeout = 5000;

    static final int maxTotal = 50;
    static final int maxPerRoute = 10;

    static final String detailHostName = "http://www.baidu.com";
    static final int detailPort = 80;
    static final int detailMaxPerRoute = 100;
}
