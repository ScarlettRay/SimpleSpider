package xyz.iamray.link.parser;


import org.apache.http.HttpEntity;

/**
 * @author liuwenrui
 * @date 2018/11/5
 */
public interface Parser<T> {

   T parse(HttpEntity entity, String chartset);
}
