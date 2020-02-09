package xyz.iamray.repo;

import org.apache.http.Header;
import xyz.iamray.exception.ExceptionWrapper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author liuwenrui
 * @date 2018/11/7
 */
public class NormalCrawlMes implements CrawlMes{

    private String curUrl;

    private List<ExceptionWrapper> exceptionWrapperzs= new LinkedList<>();

    private int retryTime;

    private Header[] responseHeaders;

    @Override
    public String getCurrentUrl() {
        return curUrl;
    }

    @Override
    public void setCurrentUrl(String currentUrl) {
        this.curUrl = currentUrl;
    }

    @Override
    public int sizeOfExceptions() {
        if(exceptionWrapperzs == null || exceptionWrapperzs.size()==0){
            return 0;
        }else{
            return exceptionWrapperzs.size();
        }
    }

    @Override
    public List<ExceptionWrapper> getExceptions() {
        return exceptionWrapperzs;
    }

    @Override
    public void addExceptionWrapper(ExceptionWrapper exceptionWrapper) {
        this.exceptionWrapperzs.add(exceptionWrapper);
    }

    @Override
    public int increamentAndGetRetryTime() {
        return retryTime++;
    }

    @Override
    public void setHeaders(Header[] headers) {
        this.responseHeaders = headers;
    }

    @Override
    public String getLastHeaderValue(String name) {
        for (int i = responseHeaders.length - 1; i >= 0; i--) {
            final Header header = responseHeaders[i];
            if (header.getName().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }

        return null;
    }

}
