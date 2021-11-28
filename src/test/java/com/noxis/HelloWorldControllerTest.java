package com.noxis;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloWorldControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldControllerTest.class);

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void helloWorldEndpointContent() {
        var response = client.toBlocking().retrieve("/hello");
        assertEquals("Hello from service", response);
    }

    @Test
    void helloWorldEndpointStatus() {
        var response = client.toBlocking().exchange("/hello", String.class);
        assertEquals("Hello from service", response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void helloFromConfigEndpointReturnsMessageFromConfigFile() {
        var response = client.toBlocking().exchange("/hello/config", String.class);
        assertEquals("Hello from application.yml", response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void returnsGreetingAsJson() {
        final ObjectNode response = client.toBlocking().retrieve("/hello/json", ObjectNode.class);
        LOG.info(response.toString());
        //assertEquals("{\"my_text\":\"Hello world\",\"id\":1,\"time_utc\":\"2021-11-28T15:26:09.694310Z\"}", response.toString());
    }

//    @Test
//    void helloFromTranslationEndpointReturnsContentFromConfigFile() {
//        var response = client.toBlocking().exchange("/hello/translation", JsonNode.class);
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("{\"en\":\"Hello World\",\"de\":\"Hallo Welt\"}", response.getBody().get().toString());
//    }
}
