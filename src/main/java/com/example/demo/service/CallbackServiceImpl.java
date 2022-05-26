package com.example.demo.service;

import com.example.demo.cache.CallbackCache;
import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.exception.TimePeriodException;
import com.example.demo.model.Callback;
import com.example.demo.model.Change;
import com.example.demo.model.Deregister;
import com.example.demo.model.Register;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.temporal.ValueRange;

@Log4j2
@Service
public class CallbackServiceImpl implements CallbackService {

    final
    CallbackCache inMemCache;

    public CallbackServiceImpl(CallbackCache inMemCache) {
        this.inMemCache = inMemCache;
    }

    @Override
    public boolean register(Register register) throws DuplicateCallbackException, TimePeriodException {
        validateTimePeriodInMiliseconds(register.getCallback());
        return inMemCache.register(register);
    }

    @Override
    public boolean change(Change change) throws DuplicateCallbackException, TimePeriodException {
        validateTimePeriodInMiliseconds(change.getCallback());
        return inMemCache.change(change);
    }

    @Override
    public boolean deRegister(Deregister deregister) {
        return inMemCache.unRegister(deregister);
    }

    /**
     * ValueRange.of(5000, 14400000) | between 5 seconds and 4 hours.
     *
     * @param callback
     * @throws TimePeriodException
     */
    private void validateTimePeriodInMiliseconds(Callback callback) throws TimePeriodException {
        if (!ValueRange.of(5000, 14400000).isValidIntValue(callback.getPeriod()))
            throw new TimePeriodException("Time period must be any time between 5 seconds and 4 hours.");
    }
}
