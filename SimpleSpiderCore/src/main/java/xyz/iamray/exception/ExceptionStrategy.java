package xyz.iamray.exception;

import xyz.iamray.repo.CrawlMes;

/**
 * Strategy to deal with exception while exception arised;
 * @author liuwenrui
 * @since 2018/11/16
 *
 */
public interface ExceptionStrategy {

    int RETRY = 0;

    int BREAKOUT = 1;

    int IGNORE = 2;

    int dealWithException(Exception e, CrawlMes crawlMes);
}
