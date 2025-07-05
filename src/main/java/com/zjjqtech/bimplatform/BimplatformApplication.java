package com.zjjqtech.bimplatform;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zao
 */
@EnableSwagger2Doc
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class BimplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BimplatformApplication.class, args);
    }

}
