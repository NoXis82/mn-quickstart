package com.noxis;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class MarketsControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void returnsListOfMarkets() {
        final List<LinkedHashMap<String, String>> response = client.toBlocking().retrieve("/markets", List.class);
        assertEquals(7, response.size());
        assertThat(response)
                .extracting(entry -> entry.get("value"))
                .containsExactlyInAnyOrder("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");
    }

}
