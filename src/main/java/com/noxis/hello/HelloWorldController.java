package com.noxis.hello;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/hello")
public class HelloWorldController {

    // method one
    // @Inject
    // private HelloWorldService helloWorldService;

    // method two
    private final MyService service;
    private final String helloFromConfig;

    public HelloWorldController(MyService service,
                                @Property(name = "hello.world.message") String helloFromConfig) {
        this.service = service;
        this.helloFromConfig = helloFromConfig;
    }

    @Get(produces = MediaType.TEXT_PLAIN)
    public String helloWorld() {
        return service.helloFromService();
    }

    @Get(uri = "/config", produces = MediaType.TEXT_PLAIN)
    public String helloConfig() {
        return helloFromConfig;
    }
}
