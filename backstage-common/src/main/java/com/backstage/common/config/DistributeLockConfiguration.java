package com.backstage.common.config;

import com.backstage.common.aspect.DistributeLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hope_Lau
 */
@Configuration
public class DistributeLockConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributeLockAspect distributeLockAspect(RedissonClient redisson){
        return new DistributeLockAspect(redisson);
    }

}
