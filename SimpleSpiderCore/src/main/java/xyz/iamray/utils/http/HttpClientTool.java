package xyz.iamray.utils.http;

/**
 * Created by Ray on 2018\2\9 0009.
 */

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iamray.core.SpiderConstant;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * org.apache.http.impl.client.CloseableHttpClient链接池生成工具
 * @reference
 * @author ray
 */
public class HttpClientTool {



    private static final Logger logger = LoggerFactory.getLogger(HttpClientTool.class);


    // org.apache.http.impl.client.CloseableHttpClient
    private static CloseableHttpClient httpclient = null;

    private static CloseableHttpClient cumstomizedHttpClient = null;

    static final int retryTime = 5;//重试次数

    static final int connectionRequestTimeout = 20000;
    static final int connectTimeout = 5000;
    static final int socketTimeout = 5000;

    static final int maxTotal = 50;
    static final int maxPerRoute = 10;

    static final String detailHostName = "http://www.baidu.com";
    static final int detailPort = 80;
    static final int detailMaxPerRoute = 100;


    /**
     * 提供参数获取httpClient对象
     * @param retryTime
     * @param connectTimeout 建立连接的超时时间
     * @return
     */
    public static CloseableHttpClient getCumstomizedHttpClient(Integer retryTime,Integer connectTimeout) {
        if (null == cumstomizedHttpClient || retryTime != HttpClientTool.retryTime || connectTimeout != HttpClientTool.connectTimeout) {
            synchronized (HttpClientTool.class) {
                if (null == cumstomizedHttpClient) {
                    cumstomizedHttpClient = init(retryTime,connectTimeout);
                }
            }
        }
        return cumstomizedHttpClient;
    }

    /**
     * 默认获取httpclient的方法 重试次数3次，建立连接的时间为5秒
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        if (null == httpclient) {
            synchronized (HttpClientTool.class) {
                if (null == httpclient) {
                    httpclient = init(5,5000);
                }
            }
        }
        return httpclient;
    }

    /**
     * 链接池初始化 这里最重要的一点理解就是. 让CloseableHttpClient 一直活在池的世界里, 但是HttpPost却一直用完就消掉.
     * 这样可以让链接一直保持着.
     * @return

    private static CloseableHttpClient init(final Integer retryTime,Integer connectTimeout ) {
        CloseableHttpClient newHttpclient = null;

        // 设置连接池
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        SSLContext sslContext =null;

        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);

        // 细化配置开始,其实这里用Map或List的for循环来配置每个链接,在特殊场景很有用.
        // 将每个路由基础的连接做特殊化配置,一般用不着
        HttpHost httpHost = new HttpHost(detailHostName, detailPort);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), detailMaxPerRoute);
        // cm.setMaxPerRoute(new HttpRoute(httpHost2),
        // detailMaxPerRoute2);//可以有细化配置2
        // cm.setMaxPerRoute(new HttpRoute(httpHost3),
        // detailMaxPerRoute3);//可以有细化配置3
        // 细化配置结束

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                logger.info(exception.getMessage()+" : 第"+ executionCount+"次重试");
                if (executionCount >= retryTime) {// 如果已经重试了retryTime次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if(exception instanceof SocketTimeoutException){ //读取超时
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return true;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        newHttpclient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).setRetryHandler(httpRequestRetryHandler).build();
        return newHttpclient;
    }
     */

    /**
     * 获取文件字节的默认API
     * @param url
     * @param header
     * @return
     */
    public static byte[] getBytesByHttpClient(String url,Map<String,String> header){

        return getBytesWithHttpClient(url,header,getHttpClient());
    }


    /**
     * 获取文件字节的API
     * @param httpClient
     * @return
     */
    public static byte[] getBytesWithHttpClient(String url,Map<String,String> header,CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry: header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            HttpEntity entity = respone.getEntity();
            in = entity.getContent();

            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


    /**
     * restful API
     */
    public static JSON getJSONByHttpClient(String url,Map<String,String> header){
        return getJSONWithHttpClient(url,header,getHttpClient());
    }

    /**
     * 自定义的httpclient,供SimpleSpider使用
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static JSON getJSONWithHttpClient(String url,Map<String,String> header, CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            HttpEntity entity = respone.getEntity();
            in = entity.getContent();

            return (JSON) JSON.parse(IOUtils.toByteArray(in));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据URL获取document
     * @param url
     * @param header
     * @return
     */
    public static Document getDocumentByHttpClient(String url,Map<String,String> header){
        return getDocumentWithHttpClient(url, header, getHttpClient());
    }

    /**
     * 根据URL获取document
     * @param url
     * @param header
     * @return
     */
    public static Document postDocumentByHttpClient(String url,Map<String,String> header,Map<String,String> postBody){
        return postDocumentWithHttpClient(url, header,postBody, getHttpClient());
    }

    /**
     * 自定义httpclient,供SimpleSpider使用
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static Document getDocumentWithHttpClient(String url, Map<String,String> header, CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        //重复
        //RequestConfig config = RequestConfig.custom().build();
        //httpGet.setConfig(config);
        return praseResponse(httpGet,url,httpClient);
    }

    /**
     * post请求
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static Document postDocumentWithHttpClient(String url, Map<String,String> header,Map<String,String> postBody, CloseableHttpClient httpClient){
        //拼接url
        HttpPost httpPost = new HttpPost(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpPost.setHeader(entry.getKey(),entry.getValue());
        }
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        if(postBody != null){
            for(Map.Entry<String,String> entry: postBody.entrySet()){
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, SpiderConstant.UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return praseResponse(httpPost,url,httpClient);

    }

    /**
     * 抽离公共部分
     * @param request
     * @param url
     * @param httpClient
     * @return
     */
    private static Document praseResponse(HttpRequestBase request, String url, CloseableHttpClient httpClient){
        InputStream in = null;
        try(CloseableHttpResponse respone = httpClient.execute(request)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                request.abort();
                return null;
            }
            HttpEntity entity = respone.getEntity();
            in = entity.getContent();

            //编码获取
            String charsetName = SpiderConstant.UTF8;
            if(entity.getContentType() != null){
                String contentType = entity.getContentType().getValue();
                if(contentType != null && contentType.indexOf("charset=") > 0){
                    charsetName = contentType.substring(contentType.indexOf("charset=")+8);
                }
            }
            //反转义
            return Jsoup.parse(StringEscapeUtils.unescapeJava(IOUtils.toString(in,charsetName)),url);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static String getInputStreamWithHttpClient(String url,Map<String,String> header,CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry: header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            HttpEntity entity = respone.getEntity();
            in = entity.getContent();
            return IOUtils.toString(in,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public static String defaultGetInputStream(String url,Map<String,String> header){
        return getInputStreamWithHttpClient(url,header,getHttpClient());
    }

}
