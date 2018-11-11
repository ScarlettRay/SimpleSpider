package xyz.iamray.repo;

import xyz.iamray.exception.ExceptionWrapper;

import java.util.List;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public interface CrawlMes {

    String getCurrentUrl();

    int sizeOfExceptions();

    List<ExceptionWrapper> getExceptions();

}
