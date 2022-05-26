package com.example.demo.cache;

import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.model.Callback;
import com.example.demo.model.Change;
import com.example.demo.model.Deregister;
import com.example.demo.model.Register;
import com.example.demo.task.CallbackTask;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * Leveraging In Memory Concurrent Map to administer callback registrations
 * Leveraging ScheduledExecutorService to execute a callback at initial fixed and changeable rate
 */
@Log4j2
public class CallbackCacheImpl implements CallbackCache {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentMap<URL, ScheduledFuture> callbackMap = new ConcurrentHashMap<>();

    public CallbackCacheImpl() {
        log.info("Webhook Cache Initializing");
    }

    @Override
    public boolean register(Register register) throws DuplicateCallbackException {
        Callback callback = register.getCallback();
        return registerCallbackInCacheAndStartTimerTask(callback);
    }

    @Override
    public boolean unRegister(Deregister deregister) {
        return removeCallbackFromCacheAndCancelTimerTask(deregister.getUrl());
    }

    @Override
    public boolean change(Change change) throws DuplicateCallbackException {
        URL url = change.getCallback().getUrl();
        Callback callback = change.getCallback();

        log.info("Changing [" + url.toExternalForm() + "]");
        return removeCallbackFromCacheAndCancelTimerTask(url)
                && registerCallbackInCacheAndStartTimerTask(callback);
    }

    private boolean registerCallbackInCacheAndStartTimerTask(Callback callback) throws DuplicateCallbackException {
        URL url = callback.getUrl();
        log.info("Registering [" + url.toExternalForm() + "]");

        if (callbackMap.containsKey(url)) {
            String errMsg = "Registering [" + url.toExternalForm() + "], failed with duplicate.";
            log.error(errMsg);
            throw new DuplicateCallbackException(errMsg);
        }

        CallbackTask callbackTask = CallbackTask.builder().received(Instant.now()).callback(callback).build();

        // Register | Timer Task
        ScheduledFuture<?> scheduledFuture = executor.
                scheduleAtFixedRate(callbackTask, callback.getPeriod(), callback.getPeriod(), TimeUnit.MILLISECONDS);

        // Register | Cache
        callbackMap.put(callback.getUrl(), scheduledFuture);

        return true;
    }

    private boolean removeCallbackFromCacheAndCancelTimerTask(URL url) {
        log.info("Un-Registering [" + url + "]");
        ScheduledFuture scheduledFuture = callbackMap.remove(url);
        if (null == scheduledFuture) return false;

        // UnRegister Timer Task
        return scheduledFuture.cancel(false);
    }
}
