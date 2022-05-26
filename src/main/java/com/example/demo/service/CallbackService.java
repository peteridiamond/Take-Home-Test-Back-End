package com.example.demo.service;

import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.exception.TimePeriodException;
import com.example.demo.model.Change;
import com.example.demo.model.Deregister;
import com.example.demo.model.Register;

public interface CallbackService {

    boolean register(Register register) throws DuplicateCallbackException, TimePeriodException;

    boolean deRegister(Deregister deregister);

    boolean change(Change change) throws DuplicateCallbackException, TimePeriodException;
}
