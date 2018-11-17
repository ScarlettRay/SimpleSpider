package xyz.iamray.repo;

import xyz.iamray.exception.ExceptionWrapper;

import java.util.List;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public interface CrawlMes {

    String getCurrentUrl();

    void setCurrentUrl(String currentUrl);

    int sizeOfExceptions();

    List<ExceptionWrapper> getExceptions();

    void addExceptionWrapper(ExceptionWrapper exceptionWrapper);

}
