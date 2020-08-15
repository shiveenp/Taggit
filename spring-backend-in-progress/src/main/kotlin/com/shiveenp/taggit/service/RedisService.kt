package com.shiveenp.taggit.service

import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Service
class RedisService {

    private val redis = Jedis()

    fun put(key: String, value: String) {
        redis.set(key.toString(), value.toString())
    }

    fun get(key: Any): String {
        return redis.get(key.toString())
    }
}
