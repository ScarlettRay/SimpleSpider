package xyz.iamray.link.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author liuwenrui
 * @since 2018/11/27
 */
public class JSONObjectParser implements Parser<JSONObject>{

    @Override
    public JSONObject parse(HttpEntity entity, String chartset) {
        try {
            return JSONObject.parseObject(EntityUtils.toString(entity, chartset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
