package xyz.iamray.exception;

import xyz.iamray.exception.spiderexceptions.SpiderException;

/**
 *
 * @author liuwenrui
 * @date 2018/11/3
 */
public class ExceptionWrapper {

    public SpiderException exception;

    public String url;

    public ExceptionWrapper(SpiderException e,String url){
        this.exception = e;
        this.url = url;
    }

}
