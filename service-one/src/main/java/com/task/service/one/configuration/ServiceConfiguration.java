package com.task.service.one.configuration;

import com.task.common.configuration.SignatureConfiguration;
import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Import(SignatureConfiguration.class)
public class ServiceConfiguration {
    @Bean
    @Qualifier("unsigned")
    public ReactiveRedisTemplate<String, byte[]> unsignedDataRedisTemplate(ReactiveRedisConnectionFactory rcf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer<byte[]> valueSerializer = RedisSerializer.byteArray();
        RedisSerializationContext.RedisSerializationContextBuilder<String, byte[]> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, byte[]> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(rcf, context);
    }

    @Bean
    @Qualifier("signed")
    public ReactiveRedisTemplate<String, SignatureData> signedDataRedisTemplate(ReactiveRedisConnectionFactory rcf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer<SignatureData> valueSerializer = new Jackson2JsonRedisSerializer<>(SignatureData.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, SignatureData> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, SignatureData> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(rcf, context);
    }

    @Bean
    @Qualifier("publisher")
    public ReactiveRedisTemplate<String, QueueInfo> queueInfoRedisTemplate(ReactiveRedisConnectionFactory rcf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<QueueInfo> valueSerializer = new Jackson2JsonRedisSerializer<>(QueueInfo.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, QueueInfo> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, QueueInfo> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(rcf, context);
    }
}
