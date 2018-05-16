package com.cloud.monitor.common.http;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HttpService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpService.class); 

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig config;
    private final static int SUCCESS = 200;

    public HttpResult doGet(String url) {
    	LOGGER.info("HttpService doGet url:{}",url);
        HttpResult result=null;
        CloseableHttpResponse response = null;
        try {
            url= URLDecoder.decode(url, "UTF-8");
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            
            response = httpClient.execute(httpGet);
            LOGGER.info("HttpService doGet response:{}",response);
            
            response.getStatusLine().getReasonPhrase();
            result= new HttpResult(response.getStatusLine().getStatusCode(),response.getStatusLine().getReasonPhrase(),null);
            if (response.getStatusLine().getStatusCode() == SUCCESS) {
                result.setData(EntityUtils.toString(response.getEntity(), "UTF-8"));
            }else{
            	LOGGER.info("HttpService doGet url:{},response:{}",url,response);
            }
        } catch (Exception e){
            result=null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            }
            catch (Exception e){
            	LOGGER.info("HttpService doGet exception:{}",e);
            }

        }
        LOGGER.info("HttpService doGet result:{}",result);
        return result;
    }
    public HttpResult doGet(String url, Map<String, String> paramMap)  {

        HttpResult result=null;
        try {
            URIBuilder builder = new URIBuilder(url);
            for (String s : paramMap.keySet()) {
                builder.addParameter(s, paramMap.get(s));
            }
            result= doGet(builder.build().toString());
        }
        catch (Exception e){
            result=null;
        }
        return result;
    }
    public HttpResult doPost(String url, Map<String, String> paramMap) {

    	LOGGER.info("HttpService doPost url:{},param:{}",url,paramMap);
        HttpResult result=null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        if (paramMap != null) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (String s : paramMap.keySet()) {
                parameters.add(new BasicNameValuePair(s, paramMap.get(s)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8"));
            httpPost.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            LOGGER.info("HttpService doPost response:{}",response);
            result= new HttpResult(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase() , EntityUtils.toString(response.getEntity()));
        }
        catch (Exception e){
        	LOGGER.info("HttpService doPost exception:{}",e);
        }
        finally {
            try{
                if (response != null) {
                    response.close();
                }
            }
            catch (Exception e){
            	LOGGER.info("HttpService doPost exception:{}",e);
            }
        }
        LOGGER.info("HttpService doPost result:{}",result);
        return result;
    }
    public HttpResult doPost(String url)  {

        HttpResult result=null;
        try {
            result = doPost(url, null);
        }
        catch (Exception e){
        }
        return result;
    }
    public HttpResult doPostJson(String url, String json) {

    	LOGGER.info("HttpService doPostJson url:{},json:{}",url,json);
        HttpResult result=null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.config);
        if (json != null) {
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(httpPost);
            LOGGER.info("HttpService doPostJson response:{}",response);
            result= new HttpResult(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), EntityUtils.toString(response.getEntity(), "UTF-8"));
        }catch (Exception e){
        	LOGGER.info("HttpService doPostJson exception:{}",e);
        }finally {
            try {
                if (response != null) {
                    response.close();
                }
            }catch (Exception e){
            	LOGGER.info("HttpService doPostJson exception:{}",e);
            }
        }
        LOGGER.info("HttpService doPostJson result:{}",result);
        return result;
    }
}