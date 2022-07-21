package edu.neu.ecommerce.recommend.util;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * MappingJackson2HttpMessageConverter
 * 修改http响应的html数据
 * HttpMessageConverter found for response type [class com.alibaba.fastjson.JSONObject] and content type [text/html;charset=utf-8]
 */
public class RestTemplateUtil {

    public static RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

        return restTemplate;
    }
}




