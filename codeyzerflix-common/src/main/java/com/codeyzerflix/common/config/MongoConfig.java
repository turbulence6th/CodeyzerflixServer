package com.codeyzerflix.common.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.codeyzerflix.common.repository")
@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        MongoCollection<Document> collection = mongoTemplate.getCollection("videos");

        IndexOptions options = new IndexOptions()
                .name("title_text_tr")
                .defaultLanguage("turkish")
                .background(true);

        // create text index on "title"
        collection.createIndex(Indexes.text("title"), options);
    }
} 