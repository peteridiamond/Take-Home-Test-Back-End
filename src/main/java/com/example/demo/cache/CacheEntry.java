package com.example.demo.cache;

import com.example.demo.task.CallbackTask;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
public class CacheEntry {

    private CallbackTask callbackTask;
    private ScheduledFuture scheduledFuture;
}
