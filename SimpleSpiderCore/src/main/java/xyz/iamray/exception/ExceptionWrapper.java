package xyz.iamray.exception;


/**
 *
 * @author liuwenrui
 * @date 2018/11/3
 */
public class ExceptionWrapper {

    public Exception exception;

    public String url;

    public ExceptionWrapper(Exception e, String url){
        this.exception = e;
        this.url = url;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("请求链接：").append(url).append(" 出现如下异常\n");
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            sb.append(stackTraceElement.toString() + "\n");
        }
        return sb.toString();
    }

}
