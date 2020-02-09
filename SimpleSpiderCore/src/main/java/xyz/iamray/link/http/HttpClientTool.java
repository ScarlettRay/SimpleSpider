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
import xyz.iamray.exception.spiderexceptions.AddressException;
import xyz.iamray.exception.spiderexceptions.NetWorkException;
import xyz.iamray.exception.spiderexceptions.SpiderException;
import xyz.iamray.link.parser.ParserMap;
import xyz.iamray.repo.CrawlMes;

import java.io.IOException;
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
    private static <T> T praseResponse(HttpRequestBase request, int httpStatus,CrawlMes crawlMes,CloseableHttpClient httpClient, Class<T> clazz) throws SpiderException{
        HttpEntity entity = null;
        try(CloseableHttpResponse respone = httpClient.execute(request)){
            if (respone.getStatusLine().getStatusCode() != httpStatus) {
                request.abort();
                return null;
            }
            entity = respone.getEntity();
            crawlMes.setHeaders(respone.getAllHeaders());
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
            throw new NetWorkException(e);
        } catch (Exception e1) {
            throw new AddressException(e1);
        }finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                throw new NetWorkException(e);
            }
        }
    }


    public static <T> T get(String url,Map<String,String> header,int httpStatus,CrawlMes crawlMes,CloseableHttpClient httpClient,Class<T> clazz) throws SpiderException {
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        return praseResponse(httpGet,httpStatus,crawlMes,httpClient,clazz);
    }

    public static <T> T get(String url,Map<String,String> header,CrawlMes crawlMes,CloseableHttpClient httpClient,Class<T> clazz){
        return get(url, header,HttpStatus.SC_OK,crawlMes,httpClient, clazz);
    }

    public static <T> T defaultGet(String url,Map<String,String> header,CrawlMes crawlMes,Class<T> clazz){
        return get(url,header,HttpStatus.SC_OK,crawlMes,getHttpClient(),clazz);
    }

    public static <T> T post(String url,Map<String,String> header,int httpStatus,Map<String,String> postBody,CrawlMes crawlMes,CloseableHttpClient httpClient,Class<T> clazz) throws SpiderException{
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
            //FIXME 一定是utf8吗
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, SpiderConstant.UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return praseResponse(httpPost,httpStatus,crawlMes,httpClient,clazz);
    }

    public static <T> T post(String url,Map<String,String> header,Map<String,String> postBody,CrawlMes crawlMes,CloseableHttpClient httpClient,Class<T> clazz){
        return post(url, header,HttpStatus.SC_OK,postBody,crawlMes,httpClient, clazz);
    }

    public static <T> T defaultPost(String url,Map<String,String> header,Map<String,String> postBody,CrawlMes crawlMes,Class<T> clazz){
        return post(url,header,HttpStatus.SC_OK,postBody,crawlMes,getHttpClient(),clazz);
    }



}
