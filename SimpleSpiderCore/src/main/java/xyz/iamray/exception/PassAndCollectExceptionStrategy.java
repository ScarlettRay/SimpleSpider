package xyz.iamray.exception;

import xyz.iamray.repo.CrawlMes;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public class PassAndCollectExceptionStrategy implements ExceptionStrategy{

    public static final PassAndCollectExceptionStrategy INSTANCE = new PassAndCollectExceptionStrategy();

    @Override
    public void dealWithException(Exception e,ExceptionStatusCode statusCode, CrawlMes crawlMes) {
        ExceptionWrapper ew = new ExceptionWrapper(e,statusCode);
        crawlMes.addExceptionWrapper(ew);
    }
}
