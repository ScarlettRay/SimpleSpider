package xyz.iamray.exception;

/**
 *
 * @author liuwenrui
 * @date 2018/11/3
 */
public class ExceptionWrapper {

    public Exception exception;

    public String mes;

    public int StatusCode;

    public ExceptionWrapper(Exception e,ExceptionStatusCode statusCode){
        this.exception = e;
        this.StatusCode = statusCode.statusCode;
        this.mes = statusCode.mes;
    }
}
