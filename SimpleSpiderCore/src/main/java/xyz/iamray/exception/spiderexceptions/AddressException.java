package xyz.iamray.exception.spiderexceptions;

/**
 * @author liuwenrui
 * @since 2018/11/18
 */
public class AddressException extends SpiderException{

    private static final String Address_Wrong = "Can not parse the host.Please checking";

    public AddressException(Exception e) {
        super(e);
        this.solution = Address_Wrong;
    }
}
