package xyz.iamray.exception.spiderexceptions;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public class SpiderException extends RuntimeException{

    protected String solution = null;

    public SpiderException(Exception e){
        super(e);
    }
}
