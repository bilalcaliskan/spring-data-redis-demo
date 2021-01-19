package com.bcaliskan.springdataredisdemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration().master(redisProperties.getMaster());
        addSentinels(redisProperties, redisSentinelConfiguration);
        return new JedisConnectionFactory(redisSentinelConfiguration);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisProperties redisProperties) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(redisProperties));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    private void addSentinels(RedisProperties redisProperties, RedisSentinelConfiguration redisSentinelConfiguration) {
        redisProperties.getNodes()
                .forEach(node -> {
                    String[] split = node.split(":");
                    String host = split[0];
                    int port = Integer.valueOf(split[1]);
                    redisSentinelConfiguration.addSentinel(RedisNode.newRedisNode()
                            .listeningAt(host, port)
                            .build());
                });
    }

}