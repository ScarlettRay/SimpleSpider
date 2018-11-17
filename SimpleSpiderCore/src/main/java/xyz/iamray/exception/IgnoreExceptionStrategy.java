package xyz.iamray.exception;

import xyz.iamray.repo.CrawlMes;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public class IgnoreExceptionStrategy implements ExceptionStrategy{

    public static final IgnoreExceptionStrategy INSTANCE = new IgnoreExceptionStrategy();

    @Override
    public void dealWithException(Exception e,ExceptionStatusCode statusCode, CrawlMes crawlMes) {
        // do nothing
    }
}
