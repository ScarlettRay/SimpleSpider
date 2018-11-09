package xyz.iamray.repo;

import xyz.iamray.exception.ExceptionWrapper;

import java.util.List;

/**
 * @author liuwenrui
 * @date 2018/11/7
 */
public class NormalCrawlMes implements CrawlMes{

    private String curUrl;

    private List<ExceptionWrapper> exceptionWrapperzs= null;

    @Override
    public String getCurrentUrl() {
        return curUrl;
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
}
