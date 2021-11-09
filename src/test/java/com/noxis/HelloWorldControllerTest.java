package com.noxis;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloWorldControllerTest {

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
        assertEquals("Hello world!!!", response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
