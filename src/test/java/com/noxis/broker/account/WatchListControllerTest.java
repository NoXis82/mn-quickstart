package com.noxis.broker.account;

import com.noxis.broker.model.Symbol;
import com.noxis.broker.model.WatchList;
import com.noxis.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.DELETE;
import static io.micronaut.http.HttpRequest.PUT;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    HttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnsEmptyWatchListForAccount() {
        final WatchList watchListResult = client.toBlocking().retrieve("/watchlist", WatchList.class);
        assertTrue(watchListResult.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList watchListResult = client.toBlocking().retrieve("/", WatchList.class);
        assertEquals(3, watchListResult.getSymbols().size());
        assertEquals(3, store.updateWatchList(TEST_ACCOUNT_ID, watchList).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        final HttpResponse<Object> added = client.toBlocking().exchange(PUT("/", watchList));

        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));

    }

    @Test
    void canDeleteWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        final HttpResponse<Object> delete = client.toBlocking().exchange(DELETE("/" + TEST_ACCOUNT_ID));

        assertEquals(HttpStatus.OK, delete.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    }

}
