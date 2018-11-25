package xyz.iamray.exception.spiderexceptions;

/**
 * @author liuwenrui
 * @since 2018/11/24
 */
public class ActionParseException extends SpiderException{

    private static final String Action_Error = "Some thing error happen in your defined action,please check!";

    public ActionParseException(Exception e) {
        super(e);
        this.solution = Action_Error;
    }
}
