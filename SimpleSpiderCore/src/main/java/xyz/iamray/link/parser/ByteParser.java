package xyz.iamray.link.parser;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import xyz.iamray.exception.spiderexceptions.SpiderException;

import java.io.IOException;

/**
 * @author liuwenrui
 * @date 2018/11/5
 */
public class ByteParser implements Parser<byte[]>{

    @Override
    public byte[] parse(HttpEntity entity, String chartset) {
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new SpiderException(e);
        }
    }
}
