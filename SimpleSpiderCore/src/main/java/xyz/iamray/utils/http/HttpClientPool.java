package xyz.iamray.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
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
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
@Slf4j
public class HttpClientPool {



    static final int retryTime = 3;//重试次数

    static final int connectionRequestTimeout = 20000;
    static final int connectTimeout = 5000;
    static final int socketTimeout = 5000;

    static final int maxTotal = 50;
    static final int maxPerRoute = 10;

    static final String detailHostName = "http://www.baidu.com";
    static final int detailPort = 80;
    static final int detailMaxPerRoute = 100;

    private static RequestConfig defaultRequestConfig = null;
    private static HttpRequestRetryHandler defaultRetryHandler = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static ExpiredHttpClientClearThread deomonThread = null;

    static {
        initConnectionManager();
        defaultConfig();
        defaultRetryHandler();
        startDeomonThread();
    }

    /**
     * 链接池初始化 这里最重要的一点理解就是. 让CloseableHttpClient 一直活在池的世界里, 但是HttpPost却一直用完就消掉.
     * 这样可以让链接一直保持着.
     * @return
     */
    private static void initConnectionManager() {
        CloseableHttpClient newHttpclient = null;

        // 设置连接池
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        SSLContext sslContext =null;

        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf).build();
        cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);

        // 细化配置开始,其实这里用Map或List的for循环来配置每个链接,在特殊场景很有用.
        // 将每个路由基础的连接做特殊化配置,一般用不着
        HttpHost httpHost = new HttpHost(detailHostName, detailPort);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), detailMaxPerRoute);
        // 细化配置结束

    }

    /**
     * 守护线程，用于清理PoolingHttpClientConnectionManager中没用的链接
     */
    private static void startDeomonThread(){
        deomonThread = new ExpiredHttpClientClearThread();
        new Thread(deomonThread).start();
    }

    private static class ExpiredHttpClientClearThread implements Runnable{

        private boolean running = true;

        @Override
        public void run(){
            while(running){
                synchronized (this){
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cm.closeExpiredConnections();
                }
            }
        }

        void shutdown(){
            synchronized (this){
                running = false;
                notify();
            }
        }
    }

    private static void defaultConfig(){

        defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();
    }

    private static void defaultRetryHandler(){
        defaultRetryHandler = getRetryHandler(retryTime);
    }

    private static HttpRequestRetryHandler getRetryHandler(final int retryTime){
        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                log.info(exception.getMessage()+" : Number"+ executionCount+" retry!");
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

        return httpRequestRetryHandler;
    }

    /**
     * <p>Using default system configure to get CloseableHttpClient
     * @return
     */
    public static CloseableHttpClient getHttpClient(){
        // 配置请求的超时设置
        return HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler(defaultRetryHandler).build();

    }

    public static CloseableHttpClient getHttpClientWithConfig(final int retryTime){
        HttpRequestRetryHandler httpRequestRetryHandler = getRetryHandler(retryTime);
        return HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler(httpRequestRetryHandler).build();
    }


    public static void restart(){
        initConnectionManager();
        defaultConfig();
        defaultRetryHandler();
        startDeomonThread();
    }

    public static void shutdown(){
        cm.close();
        deomonThread.shutdown();
    }


}
