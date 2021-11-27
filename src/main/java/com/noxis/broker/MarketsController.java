package com.noxis.broker;

import com.noxis.broker.model.Symbol;
import com.noxis.broker.store.InMemoryStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Controller("/markets")
public class MarketsController {

    private final InMemoryStore store;

    public MarketsController(InMemoryStore store) {
        this.store = store;
    }

    @Operation(summary = "Returns all available markets")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Tag(name = "markets")
    @Get("/")
    public Single<List<Symbol>> all() {
        return Single.just(store.getAllSymbols());
    }

}
