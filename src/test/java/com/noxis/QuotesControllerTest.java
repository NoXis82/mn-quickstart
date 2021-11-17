package com.noxis;

import com.noxis.broker.model.Quote;
import com.noxis.broker.model.Symbol;
import com.noxis.broker.store.InMemoryStore;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
