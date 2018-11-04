package xyz.iamray.utils.http;

/**
 * Created by Ray on 2018\2\9 0009.
 */

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import xyz.iamray.core.SpiderConstant;

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

    /**
     * 获取文件字节的默认API
     * @param url
     * @param header
     * @return
     */
    public static byte[] getBytesByHttpClient(String url,Map<String,String> header){
        return getBytesWithHttpClient(url,header,getHttpClient());
    }


    /**
     * 获取文件字节的API
     * @param httpClient
     * @return
     */
    public static byte[] getBytesWithHttpClient(String url,Map<String,String> header,CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry: header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        HttpEntity entity = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            entity = respone.getEntity();
            in = entity.getContent();

            return IOUtils.toByteArray(in);
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


    /**
     * restful API
     */
    public static JSON getJSONByHttpClient(String url,Map<String,String> header){
        return getJSONWithHttpClient(url,header,getHttpClient());
    }

    /**
     * 自定义的httpclient,供SimpleSpider使用
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static JSON getJSONWithHttpClient(String url,Map<String,String> header, CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        HttpEntity entity = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            entity = respone.getEntity();

            return (JSON) JSON.parse(EntityUtils.toByteArray(entity));
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

    /**
     * 根据URL获取document
     * @param url
     * @param header
     * @return
     */
    public static Document getDocumentByHttpClient(String url,Map<String,String> header){
        return getDocumentWithHttpClient(url, header, getHttpClient());
    }

    /**
     * 根据URL获取document
     * @param url
     * @param header
     * @return
     */
    public static Document postDocumentByHttpClient(String url,Map<String,String> header,Map<String,String> postBody){
        return postDocumentWithHttpClient(url, header,postBody, getHttpClient());
    }

    /**
     * 自定义httpclient,供SimpleSpider使用
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static Document getDocumentWithHttpClient(String url, Map<String,String> header, CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry : header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        //重复
        //RequestConfig config = RequestConfig.custom().build();
        //httpGet.setConfig(config);
        return praseResponse(httpGet,url,httpClient);
    }

    /**
     * post请求
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static Document postDocumentWithHttpClient(String url, Map<String,String> header,Map<String,String> postBody, CloseableHttpClient httpClient){
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
        return praseResponse(httpPost,url,httpClient);

    }

    /**
     * 抽离公共部分
     * @param request
     * @param url
     * @param httpClient
     * @return
     */
    private static Document praseResponse(HttpRequestBase request, String url, CloseableHttpClient httpClient){
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
            //反转义
            return Jsoup.parse(StringEscapeUtils.unescapeJava(IOUtils.toString(in,charsetName)),url);
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

    /**
     *
     * @param url
     * @param header
     * @param httpClient
     * @return
     */
    public static String getInputStreamWithHttpClient(String url,Map<String,String> header,CloseableHttpClient httpClient){
        //拼接url
        HttpGet httpGet = new HttpGet(url);
        for(Map.Entry<String,String> entry: header.entrySet()){
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        InputStream in = null;
        HttpEntity entity = null;
        try(CloseableHttpResponse respone = httpClient.execute(httpGet)){
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                httpGet.abort();
                return null;
            }
            entity = respone.getEntity();
            in = entity.getContent();
            return IOUtils.toString(in,"UTF-8");

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

    public static String defaultGetInputStream(String url,Map<String,String> header){
        return getInputStreamWithHttpClient(url,header,getHttpClient());
    }

}
