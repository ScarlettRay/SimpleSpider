package xyz.iamray.exception;

import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.repo.CrawlMes;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public class ThrowExceptionStrategy implements ExceptionStrategy{

    public static final ThrowExceptionStrategy INSTANCE = new ThrowExceptionStrategy();

    @Override
    public int dealWithException(SpiderException e, CrawlMes crawlMes) {
        throw new RuntimeException(e);
    }
}
