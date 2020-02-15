package xyz.iamray.link.parser;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import xyz.iamray.exception.spiderexceptions.SpiderException;

import java.io.IOException;

/**
 * @author liuwenrui
 * @date 2018/11/5
 */
public class JsonParser implements Parser<JSON>{
    @Override
    public JSON parse(HttpEntity entity, String chartset) {
        try {
            return (JSON) JSON.parse(EntityUtils.toByteArray(entity));
        } catch (IOException e) {
            throw new SpiderException(e);
        }
    }
}
