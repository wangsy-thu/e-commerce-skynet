package edu.neu.ecommerce.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import edu.neu.ecommerce.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public String app_id = "2021000121609721";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDBuYohDiTrPaU4lrAZeHCIe2MlVvkhEgw1Y+rRgM2ZD9TBowa/1ZlIQKk8TveHLKVph0dUUfJadG7Zha2wiFYPVSEP+SZ3PlDJ7ZgBEP28CDZWZ97gDWA1Ov5W8gW1fEuo+VJmuv51qH23dZjLt6vA3suQIcuWuePe5d/u3J8h9cdWFxDo/lZG7PwfdtsanJ+jnXz44k1kaUIsjRgM3O78iQAsdAdjewSgdx7jVeNIooZWXATaXIg2gqrSfZ7aekaC+fZj4EFI7XhlBCrsXHLWMBvxi5lrIX3YueqQmDyn9H5dBxrULt49KkUYOIDKATEaQsgDofxX6rNdcUKfu0qVAgMBAAECggEANN+N+q2gl+/wSzydfaYomKeMjOFrB4KkgGHv8hREiRUQDXWQXwVOW7ECP6qR1nvx/ZZlencPPW7v3kClAjyDuJWUe4aV4A/K/wHw7cGUbyCSrAyXgPFFo+GhD1vpAPqWr9y0h8NbtgMxpMPc2yUbJH3qc0E2DsJXljuJXekrKZowd8O3T9EGBsRi0XItPzcsKTPAZesBA436Qe7Q8D5Ng2pTsakg+yKVzL5iclwB6BloLVaPOTTIuK18wi9z149XEUxdDOciwnj8eBy3Mrf+9CZDV1m+7CkV0M3hEn+oERg4pnptfSmNQLUzgY5f9VEmoOs3jhVvy0ovI9x6E20VFQKBgQDrh26lwbRwXNPZ2O0JcZOKm2KhBFGhKtswSeM/hZ5bMIZiAvruYajeRjremiCipAADQu+FLnbjkfvSGG4kOm1AoWCpbZZz1HXN4lZaUcApgLSFf7QU2uIbGmOuA01jCIUwsUacb3jY0KYm31JB+ko+wY/iFo+N5szAgSVrH1HqmwKBgQDSj/Q+Kbsx180r3fXLK9sp5M8NVjJBytKVnW9XBHxlB3NWjxOzqNlV3SUtZc+0XnVPf7LKpDYbTCWPqxnw1BKYf9AGZlW3DEAmfpjt2gKbrvkY9kUsSbe1J2uihR/DPgXQEo3t/ZA3kdXhPfQI6qJS4WGc9XWAVftLAgdCXwiajwKBgBD0qQSJ02GbbSzrrvTVFlgOI6WP2AiQO0NYrmFuTx4leicoK8Rph2ypBafs1Jig0ddqRDAETk7Hvac8VwGIq0DR3VGVMBWan/h4aNsLs5fJSjWqId62ezyUQ8TNPA77GAuPV5P/emWxb6Swww045YtrB96w3RjMGsVpBraVhKItAoGAHrzS+8wdvo3xLoktW2ZgjQIQ07KUjZQtH5gm674E5r6qsKUcTCrG50jsJ29oXb04RM2wbPKBz4RTK2Df3Y8n08k8teJ0es84AiaS7o3XuaBWTrWMrYVpYvGJPeVPGLJSKOi8fVR5nBMW9NWVbUPLRGwQUthmOcDTeUSniKHU4LMCgYBqZ9NoFgPeVSx7wIs+mxaK8KP1TNRUVqIRq/ADLZtS8u4YkHhlg4a+CqrsTUIg6kj+VPXDaOLio0dqECi2aGF5gxEsTLPzViz/V3ftqGwQ916kpOzfgae0KCTjCEcSxWGMUQPrIz6F4djNQt2AzZZVMm+FbGha57y6MRHKr/b9yA==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuSHIWJ5EteLPYlPfnJmQG39ZIYAl0LVPvfGwpnjTLiEmDLte20c5hKKLgA7bLOuJ4mmMoR8snsvwipVcK8z57CUjKh//z2up5i+b5HcbXY4qeXfAhGNUvYhQaDui7RQ2dSFPtj5c2yTwKYogClGyJP3GE3rf4tcJDzEgLkoUkU/sUBqoNjdZeKIdkJPaU1+NLx3V28m0HblpNsGMMO+mNPBh7J77XNf28FmrQ4AEw4dXupZPx3VOhYUq7McPKzM1+sgps4BcWmWkpPfndZ2XO7fLPcAKi9moyCYgWjbHnjM2nN/N8OzEFT7ze+GU/kgDXOJUCyFIVGCC2mjDTODfywIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    public String notify_url = "http://内网穿透地址/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数
    //同步通知，支付成功，一般跳转到成功页
    public String return_url = "http://order.gulimall.com/list.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    //订单超时时间
    private String timeout = "5m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    public String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
