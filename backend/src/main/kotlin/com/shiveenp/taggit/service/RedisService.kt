package com.shiveenp.taggit.service

import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


@Service
class RedisService {

    private val jedisPool = JedisPool(JedisPoolConfig(), "localhost")

    fun put(key: String, value: String) {
        getResource().use {
            it.set(key, value)
        }
    }

    fun getOrNull(key: String): String? {
        return getResource().use {
            if (it.exists(key)) {
                it.get(key)
            } else {
                null
            }
        }
    }

    private fun getResource() = jedisPool.resource
}
