package com.appchemist.distribute_pay;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@RequiredArgsConstructor
public class MongoReactiveConfiguration extends AbstractReactiveMongoConfiguration {
    private final DistributePayConfigurationProperties properties;

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(properties.getMongodbUrl());
    }

    @Override
    protected String getDatabaseName() {
        return properties.getMongodbName();
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }
}
