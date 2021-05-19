package com.example.siyapigateway.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RefreshTokenFilter extends AbstractGatewayFilterFactory<RefreshTokenFilter.Config> {

    private final Environment env;

    public RefreshTokenFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            HttpCookie refreshCookie = request.getCookies().getFirst("Refresh");
            if(refreshCookie == null) {
                return onError(exchange, "No authorization Cookie", HttpStatus.UNAUTHORIZED);
            } else {
                String refreshToken = refreshCookie.getValue();
                if(!isValidToken(refreshToken)) {
                    return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(token).getBody()
                    .getSubject();
        } catch (Exception exception) {
            if(exception instanceof ExpiredJwtException) {
                return true;
            } else {
                return false;
            }
        }
        return true;
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
