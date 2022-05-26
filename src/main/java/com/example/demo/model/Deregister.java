package com.example.demo.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;

@Getter
@Setter
@ToString
public class Deregister {
    /**
     * Callback Record for Webhook
     */
    private @NonNull URL url;
}
