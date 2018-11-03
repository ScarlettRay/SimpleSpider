package xyz.iamray.core;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public class SpiderProperty {

    /**
     * requestConfig的参数
     */
    private Integer retryTime = 3;    //失败重试的次数

    private Integer connectTimeout = 5000;  //链接超时时间

    private Map<String,String> header = SpiderConstant.DefaultHeader;      //header参数合集

    /**
     * 用户自定义的线程池
     */
    private ExecutorService cumstomizeExecutorService;
}
