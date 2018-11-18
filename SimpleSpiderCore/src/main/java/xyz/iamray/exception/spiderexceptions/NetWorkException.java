package xyz.iamray.exception.spiderexceptions;

/**
 * @author liuwenrui
 * @since 2018/11/18
 *
 * Network error exception
 */
public class NetWorkException extends SpiderException{

    private static final String Closed_NetWork = "Your network maybe closed,please checking!";

    public NetWorkException(Exception e){
        super(e);
        this.solution = Closed_NetWork;
    }
}
