package com.example.siyapigateway.filter;

import com.example.siyapigateway.redis.service.ExpiredTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LogOutFilter  extends AbstractGatewayFilterFactory<LogOutFilter.Config> {

    private final ExpiredTokenService service;

    public LogOutFilter(Environment env, ExpiredTokenService service) {
        super(LogOutFilter.Config.class);
        this.service = service;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            HttpCookie accessCookies = request.getCookies().getFirst("Authorization");
            if(accessCookies == null) {
                return onError(exchange, "No authorization Cookie", HttpStatus.UNAUTHORIZED);
            } else {
                String accessToken = accessCookies.getValue();
                 service.addToken(accessToken);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(error);

        return response.setComplete();
    }

    public static class Config {

    }
}
