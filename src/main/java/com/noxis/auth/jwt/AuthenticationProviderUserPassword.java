package com.noxis.auth.jwt;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable final HttpRequest<?> httpRequest,
            final AuthenticationRequest<?, ?> authenticationRequest
    ) {
        return Flowable.create(flowableEmitter -> {
            final Object identity = authenticationRequest.getIdentity();
            final Object secret = authenticationRequest.getSecret();
            LOG.debug("User {} login...", identity);
            if (identity.equals("my-user") && secret.equals("secret")) {
                //pass
                flowableEmitter.onNext(AuthenticationResponse.success((String) identity, new ArrayList<>()));
                flowableEmitter.onComplete();
                return;
            }
            flowableEmitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password!")));
        }, BackpressureStrategy.ERROR);
    }

}
