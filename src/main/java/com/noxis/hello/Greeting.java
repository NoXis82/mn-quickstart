package com.noxis.hello;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Greeting {

    final String myText = "Hello world";
    final BigDecimal id = BigDecimal.ONE;
    final Instant timeUTC = Instant.now();
}
