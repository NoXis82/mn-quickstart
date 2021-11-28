package com.noxis.broker.account;

import com.noxis.broker.model.Symbol;
import com.noxis.broker.model.WatchList;
import com.noxis.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void unauthorizedAccessIsForbidden() {
        try {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
            fail("Should fail if no exception is thrown");
        } catch (HttpClientResponseException err) {
            assertEquals(HttpStatus.UNAUTHORIZED, err.getStatus());
        }

    }

    @Test
    void returnsEmptyWatchListForAccount() {
        final BearerAccessRefreshToken token = giveMyUserIsLoggedIn();
        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final WatchList watchListResult = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(watchListResult.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final BearerAccessRefreshToken token = giveMyUserIsLoggedIn();
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        var request = GET("/account/watchlist")
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final WatchList watchListResult = client.toBlocking().retrieve(request, WatchList.class);
        assertEquals(3, watchListResult.getSymbols().size());
        assertEquals(3, store.updateWatchList(TEST_ACCOUNT_ID, watchList).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final BearerAccessRefreshToken token = giveMyUserIsLoggedIn();
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        var request = PUT("/account/watchlist", watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final HttpResponse<Object> added = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        final BearerAccessRefreshToken token = giveMyUserIsLoggedIn();
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
        var request = DELETE("/account/watchlist/" + TEST_ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final HttpResponse<Object> delete = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, delete.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken giveMyUserIsLoggedIn() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        final BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        LOG.debug("Token {}, expires in {}", token.getAccessToken(), token.getExpiresIn());
        return token;
    }
}
