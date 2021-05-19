package com.example.siyapigateway.redis.repository;

import com.example.siyapigateway.redis.domain.ExpiredToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface ExpiredTokenRepository extends CrudRepository<ExpiredToken, Long> {
    ExpiredToken findFirstByValue(String value);
}
