package com.noxis.broker.account;

import com.noxis.broker.model.Symbol;
import com.noxis.broker.model.WatchList;
import com.noxis.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListReactiveControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListReactiveControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListReactiveController.ACCOUNT_ID;

    @Inject
    @Client("/")
    JWTWatchlistClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnsEmptyWatchListForAccount() {
        var login = client.login(new UsernamePasswordCredentials("my-user", "secret"));
        final Single<WatchList> result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError();

        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        var result = client.retrieveWatchList(getAuthorizationHeader()).singleOrError().blockingGet();

        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.updateWatchList(TEST_ACCOUNT_ID, watchList).getSymbols().size());
    }

    @Test
    void returnsWatchListForAccountAsSingle() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList resul = client.retrieveWatchListAsSingle(getAuthorizationHeader()).blockingGet();
        assertEquals(3, resul.getSymbols().size());
        assertEquals(3, store.updateWatchList(TEST_ACCOUNT_ID, watchList).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        final HttpResponse<Object> added = client.updateWatchList(getAuthorizationHeader(), watchList);

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

        final HttpResponse<Object> delete = client.deleteWatchList(getAuthorizationHeader(), TEST_ACCOUNT_ID);

        assertEquals(HttpStatus.OK, delete.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken getBearerAccessRefreshToken() {
        return client.login(new UsernamePasswordCredentials("my-user", "secret"));
    }

    private String getAuthorizationHeader() {
        return "Bearer " + getBearerAccessRefreshToken().getAccessToken();
    }

}
