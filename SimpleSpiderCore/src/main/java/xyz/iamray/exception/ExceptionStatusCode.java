package xyz.iamray.exception;

/**
 * @author liuwenrui
 * @since 2018/11/17
 */
public enum ExceptionStatusCode {
    CONNECTION_ERROR(0,"链接错误，请检查地址，网络是否异常");

    public int statusCode;

    public String mes;

    ExceptionStatusCode(int statusCode,String mes){
        this.statusCode = statusCode;
        this.mes = mes;
    }
}
