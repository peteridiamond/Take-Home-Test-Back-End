package com.example.demo.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;

@Getter
@Setter
@ToString
public class Callback {

    /**
     * Callback URL for Webhook registration
     */
    private @NonNull URL url;

    /**
     * Any time between 5 seconds and 4 hours | x >= 5 && x <= 4*60
     */
    private @NonNull Long period;
}
