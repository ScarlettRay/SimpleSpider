package xyz.iamray.exception;

import xyz.iamray.repo.CrawlMes;

/**
 * Strategy to deal with exception while exception arised;
 * @author liuwenrui
 * @since 2018/11/16
 *
 */
public interface ExceptionStrategy {

    void dealWithException(Exception e, ExceptionStatusCode statusCode,CrawlMes crawlMes);
}
