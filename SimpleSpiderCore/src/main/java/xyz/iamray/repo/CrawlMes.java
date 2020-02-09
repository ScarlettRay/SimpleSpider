package xyz.iamray.repo;

import org.apache.http.Header;
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

    int increamentAndGetRetryTime();

    void setHeaders(Header[] headers);

    String getLastHeaderValue(String name);

}
