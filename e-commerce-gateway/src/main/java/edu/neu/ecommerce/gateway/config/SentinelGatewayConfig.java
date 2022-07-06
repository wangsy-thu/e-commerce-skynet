package edu.neu.ecommerce.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.common.exception.BizCodeEnume;
import edu.neu.ecommerce.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelGatewayConfig {
    public SentinelGatewayConfig() {
        // 网关限流了请求，就会调用此回调
        GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) -> {
            R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
            String errJson = JSON.toJSONString(error);
            return ServerResponse.ok().body(Mono.just(errJson), String.class);
        });
    }
}
