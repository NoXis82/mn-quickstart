package com.noxis.broker;

import com.noxis.broker.error.CustomError;
import com.noxis.broker.model.Quote;
import com.noxis.broker.model.Symbol;
import com.noxis.broker.store.InMemoryStore;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void returnsQuotePerSymbol() {
        final Quote apple = initRandomQuote("AAPL");
        store.update(apple);

        final Quote amazon = initRandomQuote("AMZN");
        store.update(amazon);

        final Quote appleResponse = client.toBlocking().retrieve(GET("/quotes/AAPL"), Quote.class);
        LOG.debug("Result: {}" + appleResponse);
        assertThat(apple).isEqualToComparingFieldByField(appleResponse);

        final Quote amazonResponse = client.toBlocking().retrieve(GET("/quotes/AMZN"), Quote.class);
        LOG.debug("Result: {}" + amazonResponse);
        assertThat(amazon).isEqualToComparingFieldByField(amazonResponse);
    }

    @Test
    void returnsNotFoundOnUnsupportedSymbol() {
        try {
            client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"),
                    Argument.of(Quote.class),
                    Argument.of(CustomError.class)
            );
        } catch (HttpClientResponseException err) {
            assertEquals(HttpStatus.NOT_FOUND, err.getResponse().getStatus());
            LOG.debug("Body:{}", err.getResponse().getBody(CustomError.class));
            final Optional<CustomError> customError = err.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("quote for symbol not found...", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());
        }
    }

    private Quote initRandomQuote(String symbol) {
        return Quote.builder()
                .symbol(new Symbol(symbol))
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }

}
