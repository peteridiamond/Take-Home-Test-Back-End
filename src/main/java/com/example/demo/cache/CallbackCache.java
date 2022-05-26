package com.example.demo.cache;

import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.model.Change;
import com.example.demo.model.Deregister;
import com.example.demo.model.Register;

/**
 *
 */
public interface CallbackCache {

    boolean register(Register register) throws DuplicateCallbackException;

    boolean unRegister(Deregister deregister);

    boolean change(Change change) throws DuplicateCallbackException;
}
