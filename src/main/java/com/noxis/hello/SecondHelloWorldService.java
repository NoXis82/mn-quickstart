package com.noxis.hello;

import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

@Singleton
public class SecondHelloWorldService implements MyService {

    @Override
    public String helloFromService() {
        return "Hello from second service";
    }

}
