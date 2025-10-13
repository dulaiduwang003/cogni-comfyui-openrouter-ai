/**
 * @author 明明不是下雨天
 */
package com.cn.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * The type Redisson config.
 *
 * @author 欧渐风.
 * &#064;email  2074055628 @qq.com.
 */
@Configuration
@Slf4j
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.database}")
    private int database;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String address = "redis://" + host + ":" + port;
        singleServerConfig.setAddress(address);
        singleServerConfig.setDatabase(database);
        if (!Objects.equals(password, "")) {
            singleServerConfig.setPassword(password);
        }
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }

}
