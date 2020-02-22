package xyz.iamray.core;

import org.apache.http.HttpStatus;
import xyz.iamray.exception.ExceptionStrategy;
import xyz.iamray.exception.PassAndCollectExceptionStrategy;

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

    private ExceptionStrategy exceptionStrategy = PassAndCollectExceptionStrategy.INSTANCE;

    /**
     * 用户自定义的线程池
     */
    private ExecutorService cumstomizeExecutorService;

    /**
     * 定义要处理的状态码
     */
    private int httpStatus = HttpStatus.SC_OK;

    public Integer getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Integer retryTime) {
        this.retryTime = retryTime;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public ExceptionStrategy getExceptionStrategy() {
        return exceptionStrategy;
    }

    public void setExceptionStrategy(ExceptionStrategy exceptionStrategy) {
        this.exceptionStrategy = exceptionStrategy;
    }


    protected void setCumstomizeExecutorService(ExecutorService cumstomizeExecutorService) {
        this.cumstomizeExecutorService = cumstomizeExecutorService;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

}
