package com.noxis.broker;

import com.noxis.broker.model.Symbol;
import com.noxis.broker.store.InMemoryStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/markets")
public class MarketsController {

    private final InMemoryStore store;

    public MarketsController(InMemoryStore store) {
        this.store = store;
    }

    @Get("/")
    public List<Symbol> all() {
        return store.getAllSymbols();
    }

}
