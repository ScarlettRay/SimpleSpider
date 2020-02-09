package xyz.iamray.core;

import lombok.Data;
import org.apache.http.HttpStatus;
import xyz.iamray.exception.ExceptionStrategy;
import xyz.iamray.exception.PassAndCollectExceptionStrategy;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
@Data
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


}
