package com.noxis.broker.account;

import com.noxis.broker.model.WatchList;
import com.noxis.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist-reactive")
public class WatchListReactiveController {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListReactiveController.class);
    private final InMemoryAccountStore store;
    static final UUID ACCOUNT_ID = UUID.randomUUID();
    private final Scheduler scheduler;

    public WatchListReactiveController(
            final InMemoryAccountStore store,
            @Named(TaskExecutors.IO) ExecutorService executorService
    ) {
        this.store = store;
        this.scheduler = Schedulers.from(executorService);
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get() {
        LOG.debug("getWatchList - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Get(value = "/single", produces = MediaType.APPLICATION_JSON)
    public Flowable<WatchList> getAsSingle() {
        return Single.fromCallable(() -> {
            LOG.debug("getAsSingle - {}", Thread.currentThread().getName());
            return store.getWatchList(ACCOUNT_ID);
        }).toFlowable().subscribeOn(scheduler);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(
            value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    @ExecuteOn(TaskExecutors.IO)
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
