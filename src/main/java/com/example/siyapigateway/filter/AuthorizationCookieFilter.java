package com.example.siyapigateway.filter;

import com.example.siyapigateway.redis.domain.ExpiredToken;
import com.example.siyapigateway.redis.service.ExpiredTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthorizationCookieFilter extends AbstractGatewayFilterFactory<AuthorizationCookieFilter.Config> {

    private final Environment env;
    private final ExpiredTokenService tokenService;

    public AuthorizationCookieFilter(Environment env, ExpiredTokenService tokenService) {
        super(Config.class);
        this.env = env;
        this.tokenService = tokenService;
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
                if(!isValidToken(accessToken)) {
                    return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                } else if (tokenService.isValid(accessToken) != null) {
                    ExpiredToken token = tokenService.isValid(accessToken);
                    return onError(exchange, "Logout Token", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        Jws<Claims> subject = null;
        String secret = env.getProperty("token.secret");
        try {
            subject = Jwts.parser().setSigningKey(secret)
                    .parseClaimsJws(token);
            System.out.println(subject);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return false;
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
