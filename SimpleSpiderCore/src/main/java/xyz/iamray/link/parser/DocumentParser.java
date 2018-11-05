package xyz.iamray.link.parser;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author liuwenrui
 * @date 2018/11/5
 */
public class DocumentParser implements Parser<Document>{


    @Override
    public Document parse(HttpEntity entity, String chartset) {
        try {
            return Jsoup.parse(StringEscapeUtils.unescapeJava(EntityUtils.toString(entity,chartset)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
