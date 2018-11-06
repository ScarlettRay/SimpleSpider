package xyz.iamray.link.http;

/**
 * Created by Ray on 2018\2\9 0009.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import xyz.iamray.core.SpiderConstant;
import xyz.iamray.link.parser.ParserMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * org.apache.http.impl.client.CloseableHttpClient链接池生成工具
 * @reference
 * @author ray
 */
public class HttpClientTool extends HttpClientPool{

    private static ParserMap parserMap = ParserMap.parserMap;

    /**
     * 抽离公共部分
     * @param request
     * @param httpClient
     * @return
     */
    private static <T> T praseResponse(HttpRequestBase request, CloseableHttpClient httpClient,Class<T> clazz){
        InputStream in = null;
        HttpEntity entity = null;
        try(CloseableHttpResponse respone = httpClient.execute(request)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                request.abort();
                return null;
            }
            entity = respone.getEntity();
            in = entity.getContent();

            //编码获取
            String charsetName = SpiderConstant.UTF8;
            if(entity.getContentType() != null){
                String contentType = entity.getContentType().getValue();
                if(contentType != null && contentType.indexOf("charset=") > 0){
                    charsetName = contentType.substring(contentType.indexOf("charset=")+8);
                }
            }
            return (T)parserMap.get(clazz.getName()).parse(entity,charsetName);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static <T> T get(String url,Map<String,String> header,CloseableHttpClient httpClient,Class<T> clazz){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        return praseResponse(httpGet,httpClient,clazz);
    }

    public <T> T defultGet(String url,Map<String,String> header,Class<T> clazz){
        return get(url,header,getHttpClient(),clazz);
    }

    public static <T> T post(String url,Map<String,String> header,Map<String,String> postBody,CloseableHttpClient httpClient,Class<T> clazz){
        //拼接url
        HttpPost httpPost = new HttpPost(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpPost.setHeader(entry.getKey(),entry.getValue());
        }
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        if(postBody != null){
            for(Map.Entry<String,String> entry: postBody.entrySet()){
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, SpiderConstant.UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return praseResponse(httpPost,httpClient,clazz);
    }

    public static <T> T defaultPost(String url,Map<String,String> header,Map<String,String> postBody,Class<T> clazz){
        return post(url,header,postBody,getHttpClient(),clazz);
    }



}
