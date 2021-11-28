package com.noxis.hello;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Security;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/hello")
public class HelloWorldController {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldController.class);

    // method one
    // @Inject
    // private HelloWorldService helloWorldService;

    // method two
    private final MyService service;
    private final String helloFromConfig;
    private final HelloWorldTranslationConfig translationConfig;

    public HelloWorldController(MyService service,
                                @Property(name = "hello.world.message") String helloFromConfig,
                                HelloWorldTranslationConfig translationConfig
    ) {
        this.service = service;
        this.helloFromConfig = helloFromConfig;
        this.translationConfig = translationConfig;
    }

    @Get(produces = MediaType.TEXT_PLAIN)
    public String helloWorld() {
        LOG.debug("Called the HW API!");
        return service.helloFromService();
    }

    @Get(uri = "/config", produces = MediaType.TEXT_PLAIN)
    public String helloConfig() {
        LOG.debug("Hello from Config Message: {}", helloFromConfig);
        return helloFromConfig;
    }

    @Get(uri = "/translation", produces = MediaType.APPLICATION_JSON)
    public HelloWorldTranslationConfig helloTranslation() {
        return translationConfig;
    }

    @Get(value = "/json")
    public Greeting json() {
        return new Greeting();
    }
}
