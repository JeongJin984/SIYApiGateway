package com.example.siyapigateway.redis.service;

import com.example.siyapigateway.redis.domain.ExpiredToken;
import com.example.siyapigateway.redis.repository.ExpiredTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpiredTokenService {

    private final ExpiredTokenRepository repository;

    @Cacheable(value = "tokenCache", key = "#token")
    public ExpiredToken addToken(String token) {
        return repository.save(new ExpiredToken(token));
    }

    @Cacheable(value = "tokenCache", key = "#token" ,condition="#id!=null")
    public ExpiredToken isValid(String token) {
        return repository.findFirstByValue(token);
    }
}
