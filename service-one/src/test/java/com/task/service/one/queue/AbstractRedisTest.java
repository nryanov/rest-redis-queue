package com.task.service.one.queue;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ContextConfiguration(initializers = {AbstractRedisTest.RedisInitializer.class})
public class AbstractRedisTest {
    @Container
    public static GenericContainer container = new GenericContainer("redis:5.0").withExposedPorts(6379);

    static class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(ctx,
                    "spring.redis.host=" + container.getContainerIpAddress(),
                    "spring.redis.port=" + container.getMappedPort(6379)
            );
        }
    }
}
