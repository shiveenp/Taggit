package com.shiveenp.taggit.service

import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Service
class RedisService {

    private val redis = Jedis()

    fun put(key: String, value: String) {
        redis.set(key.toString(), value.toString())
    }

    fun getOrNull(key: String): String? {
        return if (redis.exists(key)) {
            redis.get(key.toString())
        } else {
            null
        }
    }
}
