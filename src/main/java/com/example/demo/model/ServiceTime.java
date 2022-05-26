package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class ServiceTime {
    /**
     * POST time to registered callback URL
     */
    private Instant instant = Instant.now();
}
