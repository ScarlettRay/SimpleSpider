package xyz.iamray.exception;

import xyz.iamray.repo.CrawlMes;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public class PassAndCollectExceptionStrategy implements ExceptionStrategy{

    public static final PassAndCollectExceptionStrategy INSTANCE = new PassAndCollectExceptionStrategy();

    @Override
    public int dealWithException(Exception e, CrawlMes crawlMes) {
        ExceptionWrapper ew = new ExceptionWrapper(e,crawlMes.getCurrentUrl());
        crawlMes.addExceptionWrapper(ew);
        return BREAKOUT;
    }
}
