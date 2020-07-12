package com.task.service.two.configuration;

import com.task.common.configuration.SignatureConfiguration;
import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Import(SignatureConfiguration.class)
public class ServiceConfiguration {
    @Value("${queue.destination}")
    private String destination;

    @Bean
    @Qualifier("unsigned")
    public ReactiveRedisTemplate<String, UnsignedData> unsignedDataRedisTemplate(ReactiveRedisConnectionFactory rcf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<UnsignedData> valueSerializer = new Jackson2JsonRedisSerializer<>(UnsignedData.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, UnsignedData> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, UnsignedData> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(rcf, context);
    }

    @Bean
    @Qualifier("signed")
    public ReactiveRedisTemplate<String, SignedData> signedDataRedisTemplate(ReactiveRedisConnectionFactory rcf) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<SignedData> valueSerializer = new Jackson2JsonRedisSerializer<>(SignedData.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, SignedData> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        RedisSerializationContext<String, SignedData> context = builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(rcf, context);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(MessageListener messageListener, LettuceConnectionFactory lettuceConnectionFactory){
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(lettuceConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(destination));
        return redisMessageListenerContainer;
    }
}
