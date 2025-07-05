package com.zjjqtech.bimplatform.infrastructure.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjqtech.bimplatform.model.ExtModel;
import com.zjjqtech.bimplatform.repository.ExtModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zao
 * @date 2020/09/25
 */
@Slf4j
public class SpringContextAware implements SpringApplicationRunListener {

    static AtomicReference<JsonNode> dynamicEnv = new AtomicReference<>();
    static AtomicLong version = new AtomicLong();
    static ConfigurableApplicationContext context;
    static ConfigurableEnvironment environment;

    public SpringContextAware(SpringApplication application, String[] args) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        SpringContextAware.context = context;
        SpringContextAware.environment = context.getEnvironment();
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        ExtModelRepository extModelRepository = context.getBean(ExtModelRepository.class);
        TaskScheduler taskScheduler = context.getBean(TaskScheduler.class);
        String envKey = environment.getProperty("dynamic-env.key", "dynamic-env");
        Duration period = Duration.ofMillis(environment.getProperty("dynamic-env.refresh-period", Long.class, 60000L));
        taskScheduler.scheduleAtFixedRate(() -> this.refreshDynamicConfig(extModelRepository, envKey), period);
    }

    public void refreshDynamicConfig(ExtModelRepository extModelRepository, String envKey) {
        ExtModel extModel = extModelRepository.findFirstByName(envKey);
        if (null == extModel) {
            extModel = new ExtModel();
            extModel.setName(envKey);
            extModel = extModelRepository.saveAndFlush(extModel);
            version.compareAndSet(0, extModel.getVersion());
        }
        if (version.get() != extModel.getVersion()) {
            dynamicEnv.set(extModel.getExt());
            version.set(extModel.getVersion());
        }
    }

    public static <T> T $bean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String $t(String key) {
        return $t(key, LocaleContextHolder.getLocale());
    }

    public static String $t(String key, Locale locale) {
        return context.getBean(MessageSource.class).getMessage(key, null, locale);
    }

    public static ObjectMapper $json() {
        return context.getBean(ObjectMapper.class);
    }

    public static String $env(String key) {
        return $env(key, String.class, null);
    }

    public static String $env(String key, String defaultValue) {
        return $env(key, String.class, defaultValue);
    }

    public static <T> T $env(String key, Class<T> clazz) {
        return $env(key, clazz, null);
    }

    public static <T> T $env(String key, Class<T> clazz, T defaultValue) {
        T value = environment.getProperty(key, clazz);
        if (null == value) {
            JsonNode jsonNode = dynamicEnv.get();
            if (jsonNode != null) {
                JsonNode valueNode = jsonNode.get(key);
                if (null != valueNode) {
                    try {
                        return $json().treeToValue(valueNode, clazz);
                    } catch (JsonProcessingException e) {
                        log.error("$env error, ", e);
                    }
                }
            }
        } else {
            return value;
        }
        return defaultValue;
    }
}
