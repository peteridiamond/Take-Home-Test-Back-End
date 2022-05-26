package com.example.demo.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Change {
    /**
     * Callback Record for Webhook
     */
    private @NonNull Callback callback;
}
