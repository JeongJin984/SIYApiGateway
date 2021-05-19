package com.example.siyapigateway.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@RedisHash("expiredToken")
@NoArgsConstructor
public class ExpiredToken {
    @Id
    private Long id;

    @Indexed
    private String value;

    @TimeToLive
    private Long expiration = 20L;

    public ExpiredToken(String value) {
        this.value = value;
    }
}
