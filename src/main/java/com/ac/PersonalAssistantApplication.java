package com.ac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class PersonalAssistantApplication
{

    public static void main( String[] args )
    {
        //Below construct needed to supprt JFrames
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PersonalAssistantApplication.class);
        builder.headless(false).run(args);

        //SpringApplication.run(PersonalAssistantApplication.class, args).close();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("PersonalAssistant-");
        executor.initialize();
        return executor;
    }
}
