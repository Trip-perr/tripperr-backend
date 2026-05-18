package com.tripperr.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(properties = {
        "app.security.jwt.secret=test-secret-with-at-least-thirty-two-characters!",
        "app.security.jwt.access-token-ttl-minutes=15",
        "app.security.jwt.refresh-token-ttl-days=30",
        "app.security.jwt.issuer=tripperr-api-test",
        "app.cors.allowed-origins=http://localhost:3000",
        "app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
        "app.cors.allowed-headers=*",
        "app.cors.allow-credentials=true",
        "app.cors.max-age=3600"
})
class TripperrApplicationTests {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }

    @Test
    void contextLoads() {
        // Spring context boots with Mongo and Security wired up
    }
}
